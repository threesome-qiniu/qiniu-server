package com.qiniu.gateway.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * VideoGatewayApplication
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/27
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class VideoGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoGatewayApplication.class,args);
    }
}
