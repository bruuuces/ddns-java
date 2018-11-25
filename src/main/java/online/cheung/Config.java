package online.cheung;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangbin
 * @date 2018-11-25
 */
public class Config {
    private String regionId;
    private String accessKeyId;
    private String accessKeySecret;
    private String getIpUrl;
    private int interval;
    private String domainName;
    private List<DescribeDomainRecord> describeDomainRecords = new ArrayList<>();

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getGetIpUrl() {
        return getIpUrl;
    }

    public void setGetIpUrl(String getIpUrl) {
        this.getIpUrl = getIpUrl;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public List<DescribeDomainRecord> getDescribeDomainRecords() {
        return describeDomainRecords;
    }

    public void setDescribeDomainRecords(
            List<DescribeDomainRecord> describeDomainRecords) {
        this.describeDomainRecords = describeDomainRecords;
    }

    public static class DescribeDomainRecord {
        private String resourceRecord;
        private String type;

        public String getResourceRecord() {
            return resourceRecord;
        }

        public void setResourceRecord(String resourceRecord) {
            this.resourceRecord = resourceRecord;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
