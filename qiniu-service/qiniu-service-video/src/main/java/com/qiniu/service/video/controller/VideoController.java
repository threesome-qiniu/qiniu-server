package com.qiniu.service.video.controller;

import com.qiniu.common.core.domain.R;
import com.qiniu.model.video.service.IVideoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 视频表(Video)表控制层
 *
 * @author roydon
 * @since 2023-10-25 20:33:08
 */
@RestController
@RequestMapping("/video")
public class VideoController {

    @Resource
    private IVideoService videoService;

    /**
     * 视频上传
     */
    @PostMapping("/upload")
    public R<?> uploadVideo(MultipartFile file){

        return R.ok();
    }

}

