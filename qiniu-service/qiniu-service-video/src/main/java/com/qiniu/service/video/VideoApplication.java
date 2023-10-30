package com.qiniu.service.video;

import com.qiniu.common.annotations.EnableRedisConfig;
import com.qiniu.common.annotations.EnableUserTokenInterceptor;
import com.qiniu.common.config.MybatisPlusConfig;
import com.qiniu.common.swagger.Swagger2Configuration;
import com.qiniu.feign.user.RemoteUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

/**
 * VideoApplication
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/25
 **/

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {RemoteUserService.class})
@EnableUserTokenInterceptor
@EnableRedisConfig
@Import({MybatisPlusConfig.class,Swagger2Configuration.class})
public class VideoApplication {
    public static void main(String[] args) {

        SpringApplication.run(VideoApplication.class,args);


    }
}
