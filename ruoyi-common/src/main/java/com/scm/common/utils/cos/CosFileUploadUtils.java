package com.scm.common.utils.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import com.scm.common.config.CosConfig;
import com.scm.common.utils.DateUtils;
import com.scm.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;

/**
 * 腾讯云COS文件上传工具类
 *
 * @author scm
 */
@Component
public class CosFileUploadUtils
{
    private static final Logger log = LoggerFactory.getLogger(CosFileUploadUtils.class);

    @Autowired
    private CosConfig cosConfig;

    private COSClient cosClient;

    /**
     * 初始化COS客户端
     */
    @PostConstruct
    public void init()
    {
        if (!cosConfig.isEnabled())
        {
            log.info("腾讯云COS未启用，使用本地文件系统上传");
            return;
        }

        try
        {
            // 1. 初始化用户身份信息
            COSCredentials cred = new BasicCOSCredentials(cosConfig.getSecretId(), cosConfig.getSecretKey());

            // 2. 设置bucket的区域
            ClientConfig clientConfig = new ClientConfig(new Region(cosConfig.getRegion()));

            // 3. 生成cos客户端
            cosClient = new COSClient(cred, clientConfig);

            log.info("腾讯云COS客户端初始化成功，区域：{}，存储桶：{}", cosConfig.getRegion(), cosConfig.getBucketName());
        }
        catch (Exception e)
        {
            log.error("腾讯云COS客户端初始化失败", e);
            cosClient = null;
        }
    }

    /**
     * 销毁COS客户端
     */
    @PreDestroy
    public void destroy()
    {
        if (cosClient != null)
        {
            cosClient.shutdown();
            log.info("腾讯云COS客户端已关闭");
        }
    }

    /**
     * 上传文件到COS
     *
     * @param file 上传的文件
     * @param customPath 自定义路径（可选）
     * @return 文件访问URL
     * @throws IOException 上传失败时抛出
     */
    public String upload(MultipartFile file, String customPath) throws IOException
    {
        return upload(file, customPath, null);
    }

    /**
     * 上传文件到COS
     *
     * @param file 上传的文件
     * @param customPath 自定义路径（可选）
     * @param fileName 自定义文件名（可选，不指定则使用原文件名）
     * @return 文件访问URL
     * @throws IOException 上传失败时抛出
     */
    public String upload(MultipartFile file, String customPath, String fileName) throws IOException
    {
        if (!cosConfig.isEnabled())
        {
            throw new IllegalStateException("腾讯云COS未启用");
        }

        if (cosClient == null)
        {
            throw new IllegalStateException("COS客户端未初始化");
        }

        if (file == null || file.isEmpty())
        {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        InputStream inputStream = null;
        try
        {
            // 构建文件路径
            String finalFileName = StringUtils.isNotEmpty(fileName) ? fileName : file.getOriginalFilename();
            String objectKey = buildObjectKey(customPath, finalFileName);

            // 设置元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            inputStream = file.getInputStream();

            // 上传文件
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    cosConfig.getBucketName(),
                    objectKey,
                    inputStream,
                    metadata
            );

            cosClient.putObject(putObjectRequest);

            // 返回访问URL
            String fileUrl = buildFileUrl(objectKey);
            log.info("文件上传到COS成功，文件名：{}，访问URL：{}", finalFileName, fileUrl);
            return fileUrl;

        }
        catch (Exception e)
        {
            log.error("文件上传到COS失败", e);
            throw new IOException("文件上传失败：" + e.getMessage(), e);
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    log.error("关闭输入流失败", e);
                }
            }
        }
    }

    /**
     * 上传文件到COS（使用默认路径）
     *
     * @param file 上传的文件
     * @return 文件访问URL
     * @throws IOException 上传失败时抛出
     */
    public String upload(MultipartFile file) throws IOException
    {
        return upload(file, null);
    }

    /**
     * 删除COS上的文件
     *
     * @param fileUrl 文件访问URL
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileUrl)
    {
        if (!cosConfig.isEnabled() || cosClient == null)
        {
            log.warn("COS未启用或客户端未初始化，无法删除文件");
            return false;
        }

        try
        {
            // 从URL中提取objectKey
            String objectKey = extractObjectKeyFromUrl(fileUrl);
            if (StringUtils.isEmpty(objectKey))
            {
                log.warn("无法从URL中提取objectKey：{}", fileUrl);
                return false;
            }

            cosClient.deleteObject(cosConfig.getBucketName(), objectKey);
            log.info("文件删除成功，objectKey：{}", objectKey);
            return true;

        }
        catch (Exception e)
        {
            log.error("文件删除失败，fileUrl：{}", fileUrl, e);
            return false;
        }
    }

    /**
     * 构建COS对象的Key
     *
     * @param customPath 自定义路径
     * @param fileName 文件名
     * @return 对象Key
     */
    private String buildObjectKey(String customPath, String fileName)
    {
        StringBuilder keyBuilder = new StringBuilder();

        // 添加基础路径
        if (StringUtils.isNotEmpty(cosConfig.getBasePath()))
        {
            keyBuilder.append(cosConfig.getBasePath()).append("/");
        }

        // 添加自定义路径或日期路径
        if (StringUtils.isNotEmpty(customPath))
        {
            keyBuilder.append(customPath).append("/");
        }
        else
        {
            // 使用日期路径：年/月/日
            keyBuilder.append(DateUtils.datePath()).append("/");
        }

        // 添加文件名
        keyBuilder.append(fileName);

        return keyBuilder.toString();
    }

    /**
     * 构建文件访问URL
     *
     * @param objectKey 对象Key
     * @return 访问URL
     */
    private String buildFileUrl(String objectKey)
    {
        if (StringUtils.isNotEmpty(cosConfig.getBucketDomain()))
        {
            // 使用自定义域名
            String domain = cosConfig.getBucketDomain();
            // 检测是否已包含协议，如果没有则添加 https://
            if (!domain.startsWith("http://") && !domain.startsWith("https://"))
            {
                domain = "https://" + domain;
            }
            // 确保域名不以斜杠结尾，并正确拼接路径
            if (domain.endsWith("/"))
            {
                return domain + objectKey;
            }
            else
            {
                return domain + "/" + objectKey;
            }
        }
        else
        {
            // 使用COS默认域名
            return String.format("https://%s.cos.%s.myqcloud.com/%s",
                    cosConfig.getBucketName(),
                    cosConfig.getRegion(),
                    objectKey);
        }
    }

    /**
     * 从URL中提取objectKey
     *
     * @param fileUrl 文件URL
     * @return 对象Key
     */
    private String extractObjectKeyFromUrl(String fileUrl)
    {
        if (StringUtils.isEmpty(fileUrl))
        {
            return null;
        }

        try
        {
            // 移除协议头
            String url = fileUrl.replace("http://", "").replace("https://", "");

            // 找到第一个斜杠的位置
            int firstSlashIndex = url.indexOf('/');
            if (firstSlashIndex > 0 && firstSlashIndex < url.length() - 1)
            {
                return url.substring(firstSlashIndex + 1);
            }
        }
        catch (Exception e)
        {
            log.error("解析URL失败：{}", fileUrl, e);
        }

        return null;
    }

    /**
     * 判断是否启用COS
     *
     * @return true=已启用，false=未启用
     */
    public static boolean isCosEnabled()
    {
        // 这里需要从Spring容器中获取bean
        return false; // 暂时返回false，实际使用时通过@Autowired注入后调用isEnabled()
    }

    /**
     * 判断是否启用COS（实例方法）
     *
     * @return true=已启用，false=未启用
     */
    public boolean isEnabled()
    {
        return cosConfig.isEnabled() && cosClient != null;
    }
}
