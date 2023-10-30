package com.qiniu.service.video;


import com.qiniu.feign.user.RemoteUserService;
import com.qiniu.service.video.service.IVideoCategoryService;
import com.qiniu.service.video.service.IVideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/10/29 16:07
 */
@SpringBootTest
public class VideoTestApplication {

    @Autowired
    IVideoService videoService;

    @Autowired
    private IVideoCategoryService videoCategoryService;

    @Resource
    private RemoteUserService remoteUserService;

//    void bindTest(){
//        VideoBindDto videoBindDto = new VideoBindDto();
//        videoBindDto.setVideoId(1);
//        videoService.bindVideoAndUser();
//    }

    @Test
    void getUser() {
        videoCategoryService.saveVideoCategoriesToRedis();

    }

}
