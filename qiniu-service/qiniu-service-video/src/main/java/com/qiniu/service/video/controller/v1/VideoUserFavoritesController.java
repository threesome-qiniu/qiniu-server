package com.qiniu.service.video.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qiniu.common.domain.R;
import com.qiniu.common.domain.vo.PageDataInfo;
import com.qiniu.model.video.domain.VideoUserFavorites;
import com.qiniu.model.video.dto.VideoPageDto;
import com.qiniu.model.video.vo.VideoUserVo;
import com.qiniu.service.video.mapper.VideoMapper;
import com.qiniu.service.video.service.IVideoService;
import com.qiniu.service.video.service.IVideoUserFavoritesService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 视频收藏表(VideoUserFavorites)表控制层
 *
 * @author lzq
 * @since 2023-10-31 15:57:37
 */
@RestController
@RequestMapping("/api/v1/favorite")
public class VideoUserFavoritesController {

    @Resource
    private IVideoUserFavoritesService videoUserFavoritesService;

    @Resource
    private IVideoService videoService;

    /**
     * 用户收藏
     *
     * @param videoId
     * @return
     */
    @GetMapping("/{videoId}")
    public R<Boolean> getDetails(@PathVariable("videoId") String videoId) {

        return R.ok(videoUserFavoritesService.videoFavorites(videoId));
    }

    @GetMapping("/user/{userId}")
    public R<List<VideoUserVo>> getUserLikes(@PathVariable("userId") Long userId) {
        List<VideoUserVo> videoUserVos = videoUserFavoritesService.userFavorites(userId);
        return R.ok(videoUserVos);
    }

    @PostMapping("/myfavoritepage")
    public PageDataInfo myFavoritePage(@RequestBody VideoPageDto pageDto) {
        IPage<VideoUserFavorites> favoritesPage = videoUserFavoritesService.queryFavoritePage(pageDto);
        List<String> videoIds = favoritesPage.getRecords().stream().map(VideoUserFavorites::getVideoId).collect(Collectors.toList());
        return PageDataInfo.genPageData(videoService.queryVideoByVideoIds(videoIds), favoritesPage.getTotal());
    }
}

