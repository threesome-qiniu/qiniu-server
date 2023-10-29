package com.qiniu.service.video.controller.v1;


import com.qiniu.common.domain.R;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.user.domain.dto.UserThreadLocalUtil;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.domain.dto.VideoBindDto;
import com.qiniu.service.video.interceptor.VideoTokenInterceptor;
import com.qiniu.service.video.service.IVideoService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.qiniu.common.utils.JwtUtil;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    private HttpServletRequest request;


    /**
     * 视频上传
     */
    @PostMapping("/upload")
    public R<?> uploadVideo(@RequestParam("file") MultipartFile file) {
        String url = videoService.uploadVideo(file);
        return R.ok(url);
    }

    @PostMapping("/bind")
    public R<?> bindVideoAndUser(@RequestBody VideoBindDto videoBindDto) {
        User user = UserThreadLocalUtil.getUser();
        Video video = videoService.bindVideoAndUser(videoBindDto, user);
        return R.ok(video);
    }
}

