package online.cheung;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.ho.yaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class App {

    public static void main(String[] args) throws IOException {

        File configFile = new File(System.getProperty("user.dir") + "/conf/config.yaml");

        Config config = Yaml.loadType(configFile, Config.class);

        String regionId = config.getRegionId(); //必填固定值，必须为“cn-hanghou”
        String accessKeyId = config.getAccessKeyId(); // your accessKey
        String accessKeySecret = config.getAccessKeySecret();// your accessSecret
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        // 若报Can not find endpoint to access异常，请添加以下此行代码
        // DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Alidns", "alidns.aliyuncs.com");
        IAcsClient client = new DefaultAcsClient(profile);

        Runnable run = () -> {

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(config.getGetIpUrl());
            String currentIp = null;
            try (CloseableHttpResponse response = httpClient.execute(httpget)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    currentIp = EntityUtils.toString(entity);
                    System.out.println("currentIp " + currentIp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (currentIp == null) {
                return;
            }
            for (Config.DescribeDomainRecord describeDomainRecord : config
                    .getDescribeDomainRecords()) {
                String recordIp = null;
                String recordId = null;
                {
                    DescribeDomainRecordsRequest request = new DescribeDomainRecordsRequest();
                    request.setDomainName(config.getDomainName());
                    request.setTypeKeyWord(describeDomainRecord.getType());
                    request.setRRKeyWord(describeDomainRecord.getResourceRecord());
                    DescribeDomainRecordsResponse response = null;
                    try {
                        response = client.getAcsResponse(request);
                    } catch (ClientException e) {
                        e.printStackTrace();
                    }
                    List<DescribeDomainRecordsResponse.Record> list = response.getDomainRecords();
                    if (list.size() > 0) {
                        DescribeDomainRecordsResponse.Record record = list.get(0);
                        if (record != null) {
                            recordIp = record.getValue();
                            recordId = record.getRecordId();
                            System.out.println("recordIp " + recordIp);
                        }
                    }
                }
                if (recordIp == null) {
                    System.out.println("record not found");
                    continue;
                }
                if (!currentIp.equals(recordIp)) {
                    UpdateDomainRecordRequest request = new UpdateDomainRecordRequest();
                    request.setRecordId(recordId);
                    request.setRR(describeDomainRecord.getResourceRecord());
                    request.setType(describeDomainRecord.getType());
                    request.setValue(currentIp);
                    try {
                        client.getAcsResponse(request);
                    } catch (ClientException e) {
                        e.printStackTrace();
                    }
                    System.out.println("update " + currentIp);
                }
            }
        };

        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService
                .scheduleAtFixedRate(run, 0, config.getInterval(), TimeUnit.SECONDS);

    }
}
