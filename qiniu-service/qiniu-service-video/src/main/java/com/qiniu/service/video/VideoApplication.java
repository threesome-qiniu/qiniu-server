package com.qiniu.service.video;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * VideoApplication
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/25
 **/
@Slf4j
@SpringBootApplication(scanBasePackages = {
        "com.qiniu.service.video",
        "com.qiniu.model.video"
})
@MapperScan("com.qiniu.model.video.mapper")
@EnableDiscoveryClient
public class VideoApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoApplication.class,args);
    }
}
