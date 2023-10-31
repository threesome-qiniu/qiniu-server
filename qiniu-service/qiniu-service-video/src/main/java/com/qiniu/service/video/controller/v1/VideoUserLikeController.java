package com.qiniu.service.video.controller.v1;

import com.qiniu.common.domain.R;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.domain.VideoUserLike;
import com.qiniu.model.video.vo.VideoUserLikeVo;
import com.qiniu.service.video.service.IVideoUserLikeService;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
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

    @GetMapping("/user/like/{userId}")
    public R<List<VideoUserLikeVo>> getUserLikes(@PathVariable("userId") Long userId) {
        List<VideoUserLikeVo> videoUserLikeVos = videoUserLikeService.userLikes(userId);
        return R.ok(videoUserLikeVos);
    }
}

