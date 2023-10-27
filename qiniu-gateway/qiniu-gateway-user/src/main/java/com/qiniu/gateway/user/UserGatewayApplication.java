package com.qiniu.gateway.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * UserGatewayApplication
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/27
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class UserGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserGatewayApplication.class,args);
    }
}
