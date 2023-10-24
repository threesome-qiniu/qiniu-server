package com.qiniu.service.user.controller;

import com.qiniu.service.user.config.NacosConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * HelloController
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@Slf4j
@RestController
@RequestMapping
public class HelloController {

    @Resource
    private NacosConfig nacosConfig;

    @GetMapping("/hello")
    public String hello(){
        String host = nacosConfig.getHost();
        log.debug(host);
        return host;
    }


}
