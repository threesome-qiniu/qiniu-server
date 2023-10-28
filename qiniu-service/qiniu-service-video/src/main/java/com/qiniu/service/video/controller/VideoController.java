package com.qiniu.service.video.controller;

import com.qiniu.common.domain.R;
import com.qiniu.common.log.annotation.Log;
import com.qiniu.common.log.enums.BusinessType;
import com.qiniu.common.log.enums.OperatorType;
import com.qiniu.service.video.service.IVideoService;
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
public class VideoController {

    @Resource
    private IVideoService videoService;

    /**
     * 视频上传
     */
    @Log(title = "视频上传", businessType = BusinessType.INSERT, operatorType = OperatorType.MOBILE)
    @PostMapping("/upload")
    public R<?> uploadVideo(@RequestParam("file") MultipartFile file) {
        String url = videoService.uploadVideo(file);
        return R.ok(url);
    }

}

