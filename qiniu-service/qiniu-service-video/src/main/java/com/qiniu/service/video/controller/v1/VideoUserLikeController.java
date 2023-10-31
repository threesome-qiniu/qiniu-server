package com.qiniu.service.video.controller.v1;

import com.qiniu.common.domain.R;
import com.qiniu.model.video.vo.VideoUserVo;
import com.qiniu.service.video.service.IVideoUserLikeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 点赞表(VideoUserLike)表控制层
 *
 * @author lzq
 * @since 2023-10-30 14:32:56
 */
@RestController
@RequestMapping("/api/v1")
public class VideoUserLikeController {

    @Resource
    private IVideoUserLikeService videoUserLikeService;


    /**
     * 用户点赞
     * @param videoId
     * @return
     */
    @GetMapping("/like/{videoId}")
    public R<Boolean> getDetails(@PathVariable("videoId") String videoId) {
        return R.ok(videoUserLikeService.videoLike(videoId));
    }

    /**
     * 用户点赞分页查询
     * @param userId
     * @return
     */
    @GetMapping("/user/like/{userId}")
    public R<List<VideoUserVo>> getUserLikes(@PathVariable("userId") Long userId) {
        List<VideoUserVo> videoUserVos = videoUserLikeService.userLikes(userId);
        return R.ok(videoUserVos);
    }
}

