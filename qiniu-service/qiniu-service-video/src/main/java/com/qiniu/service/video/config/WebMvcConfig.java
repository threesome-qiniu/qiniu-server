package com.qiniu.service.video.config;

import com.qiniu.service.video.interceptor.VideoTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/10/29 17:17
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new VideoTokenInterceptor());
    }
}
