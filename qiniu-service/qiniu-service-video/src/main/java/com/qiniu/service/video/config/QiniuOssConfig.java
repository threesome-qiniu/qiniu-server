package com.qiniu.service.video.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * QiniuOssConfig
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/26
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "qiniu.oss")
public class QiniuOssConfig {
    private String accessKey;
    private String secretKey;
    private String bucket;
}
