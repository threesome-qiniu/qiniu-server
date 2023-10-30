package com.qiniu.service.video.controller.v1;

import com.qiniu.common.domain.R;
import com.qiniu.model.video.domain.VideoUserLike;
import com.qiniu.service.video.service.IVideoUserLikeService;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 点赞表(VideoUserLike)表控制层
 *
 * @author lzq
 * @since 2023-10-30 14:32:56
 */
@RestController
@RequestMapping("/videoUserLike")
public class VideoUserLikeController {

    @Resource
    private IVideoUserLikeService videoUserLikeService;


    @GetMapping("/like/{videoId}")
    public R<Boolean> getDetails(@PathVariable("videoId") String videoId) {
        return R.ok(videoUserLikeService.videoLike(videoId));
    }

}

