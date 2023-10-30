package com.qiniu.service.video;

import com.qiniu.model.video.domain.dto.VideoBindDto;
import com.qiniu.service.video.service.IVideoService;
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

    @Resource
            private RemoteUserService remoteUserService;

//    void bindTest(){
//        VideoBindDto videoBindDto = new VideoBindDto();
//        videoBindDto.setVideoId(1);
//        videoService.bindVideoAndUser();
//    }

    @Test
    void getUser(){
        R<User> r = remoteUserService.userInfoById(2L);
        System.out.println("r = " + r.getData().getAvatar());
    }

}
