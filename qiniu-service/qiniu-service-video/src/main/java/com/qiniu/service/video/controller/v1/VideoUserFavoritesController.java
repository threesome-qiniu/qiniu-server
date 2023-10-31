package com.qiniu.service.video.controller.v1;

import com.qiniu.common.domain.R;
import com.qiniu.model.video.vo.VideoUserVo;
import com.qiniu.service.video.service.IVideoUserFavoritesService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 视频收藏表(VideoUserFavorites)表控制层
 *
 * @author lzq
 * @since 2023-10-31 15:57:37
 */
@RestController
@RequestMapping("/api/v1")
public class VideoUserFavoritesController {
    
    @Resource
    private IVideoUserFavoritesService videoUserFavoritesService;

    /**
     * 用户收藏
     * @param videoId
     * @return
     */
    @GetMapping("/favority/{videoId}")
    public R<Boolean> getDetails(@PathVariable("videoId") String videoId) {

        return R.ok(videoUserFavoritesService.videoFavorites(videoId));
    }

    @GetMapping("/user/favority/{userId}")
    public R<List<VideoUserVo>> getUserLikes(@PathVariable("userId") Long userId) {
        List<VideoUserVo> videoUserVos = videoUserFavoritesService.userFavorites(userId);
        return R.ok(videoUserVos);
    }

}

