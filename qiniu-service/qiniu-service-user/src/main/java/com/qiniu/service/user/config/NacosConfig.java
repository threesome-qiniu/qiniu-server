package com.qiniu.service.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * NacosConfig
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@Data
@RefreshScope
@Configuration
@ConfigurationProperties("spring.redis")
public class NacosConfig {
    private String host;
}
