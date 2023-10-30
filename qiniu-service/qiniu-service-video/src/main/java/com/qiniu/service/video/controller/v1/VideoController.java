package com.qiniu.service.video.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiniu.common.domain.R;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.user.dto.UserThreadLocalUtil;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.domain.dto.VideoPageDto;
import com.qiniu.model.video.domain.dto.VideoBindDto;
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

    /**
     * 将用户上传的视频和用户信息绑定到一起
     *
     * @param videoBindDto
     * @return
     */
    @PostMapping("/bind")
    public R<Video> bindVideoAndUser(@RequestBody VideoBindDto videoBindDto) {
        Video video = videoService.bindVideoAndUser(videoBindDto);
        return R.ok(video);
    }

    /**
     * 分页查询我的视频
     *
     * @param pageDto
     * @return
     */
    @PostMapping("/mypage")
    public R<?> myPage(@RequestBody VideoPageDto pageDto) {
        return R.ok(videoService.queryMyVideoPage(pageDto));
    }


    /**
     * 分页查询用户视频
     *
     * @param pageDto
     * @return
     */
    @PostMapping("/userpage")
    public R<?> userPage(@RequestBody VideoPageDto pageDto) {
        return R.ok(videoService.queryUserVideoPage(pageDto));
    }

}

