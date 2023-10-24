package com.qiniu.service.user;

import com.qiniu.service.user.config.NacosConfig;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import javax.annotation.Resource;

/**
 * UserApplication
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@Slf4j
@SpringBootApplication(scanBasePackages = {
        "com.qiniu.service.user",
        "com.qiniu.model.user"
})
@MapperScan("com.qiniu.model.user.mapper")
@EnableDiscoveryClient
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
