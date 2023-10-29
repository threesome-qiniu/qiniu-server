package com.qiniu.service.video.controller.v1;

import com.qiniu.common.domain.R;
import com.qiniu.service.video.service.IVideoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

/**
 * 视频表(Video)表控制层
 *
 * @author roydon
 * @since 2023-10-25 20:33:08
 */
@RestController
@RequestMapping("/api/v1")
public class VideoController {

    @Resource
    private IVideoService videoService;

    /**
     * 视频上传
     */
    @PostMapping("/upload")
    public R<?> uploadVideo(@RequestParam("file") MultipartFile file) {
        String url = videoService.uploadVideo(file);
        return R.ok(url);
    }

}
