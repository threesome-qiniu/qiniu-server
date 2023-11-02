package com.qiniu.service.video.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qiniu.common.domain.R;
import com.qiniu.common.domain.vo.PageDataInfo;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.domain.VideoUserLike;
import com.qiniu.model.video.dto.VideoPageDto;
import com.qiniu.service.video.service.IVideoService;
import com.qiniu.service.video.service.IVideoUserLikeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 点赞表(VideoUserLike)表控制层
 *
 * @author lzq
 * @since 2023-10-30 14:32:56
 */
@RestController
@RequestMapping("/api/v1/like")
public class VideoUserLikeController {

    @Resource
    private IVideoUserLikeService videoUserLikeService;

    @Resource
    private IVideoService videoService;

    /**
     * 用户点赞
     *
     * @param videoId
     * @return
     */
    @GetMapping("/{videoId}")
    public R<Boolean> getDetails(@PathVariable("videoId") String videoId) {
        return R.ok(videoUserLikeService.videoLike(videoId));
    }

    /**
     * 用户点赞分页查询
     */
    @PostMapping("/mylikepage")
    public PageDataInfo myLikePage(@RequestBody VideoPageDto pageDto) {
        IPage<VideoUserLike> likeIPage = videoUserLikeService.queryMyLikeVideoPage(pageDto);
        List<String> videoIds = likeIPage.getRecords().stream().map(VideoUserLike::getVideoId).collect(Collectors.toList());
        List<Video> videos = videoService.queryVideoByVideoIds(videoIds);
        return PageDataInfo.genPageData(videos, likeIPage.getTotal());
    }

}

