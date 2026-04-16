package com.scm.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 腾讯云COS对象存储配置
 *
 * @author scm
 */
@Component
@ConfigurationProperties(prefix = "cos")
public class CosConfig
{
    /** 是否启用COS上传 */
    private boolean enabled;

    /** SecretId */
    private String secretId;

    /** SecretKey */
    private String secretKey;

    /** 地域 */
    private String region;

    /** 存储桶名称 */
    private String bucketName;

    /** 存储桶访问域名 */
    private String bucketDomain;

    /** 上传文件的基础路径 */
    private String basePath;

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getSecretId()
    {
        return secretId;
    }

    public void setSecretId(String secretId)
    {
        this.secretId = secretId;
    }

    public String getSecretKey()
    {
        return secretKey;
    }

    public void setSecretKey(String secretKey)
    {
        this.secretKey = secretKey;
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion(String region)
    {
        this.region = region;
    }

    public String getBucketName()
    {
        return bucketName;
    }

    public void setBucketName(String bucketName)
    {
        this.bucketName = bucketName;
    }

    public String getBucketDomain()
    {
        return bucketDomain;
    }

    public void setBucketDomain(String bucketDomain)
    {
        this.bucketDomain = bucketDomain;
    }

    public String getBasePath()
    {
        return basePath;
    }

    public void setBasePath(String basePath)
    {
        this.basePath = basePath;
    }
}
