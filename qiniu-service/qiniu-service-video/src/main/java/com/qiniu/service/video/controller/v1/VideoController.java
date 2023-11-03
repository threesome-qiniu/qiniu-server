package com.qiniu.service.video.controller.v1;

import com.qiniu.common.domain.R;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.dto.VideoPublishDto;
import com.qiniu.model.video.dto.VideoFeedDTO;
import com.qiniu.model.video.dto.VideoPageDto;
import com.qiniu.model.video.vo.VideoUploadVO;
import com.qiniu.model.video.vo.VideoVO;
import com.qiniu.service.video.service.IVideoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

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
     * 视频流接口
     */
    @PostMapping("/feed")
    public R<VideoVO> feed(@RequestBody VideoFeedDTO videoFeedDTO) {
        return R.ok(videoService.feedVideo(videoFeedDTO));
    }

    /**
     * 视频上传
     */
    @PostMapping("/upload")
    public R<VideoUploadVO> uploadVideo(@RequestParam("file") MultipartFile file) {
        return R.ok(videoService.uploadVideo(file));
    }

    /**
     * 将用户上传的视频和用户信息绑定到一起
     *
     * @param videoPublishDto
     * @return
     */
    @PostMapping("/publish")
    public R<?> videoPublish(@RequestBody VideoPublishDto videoPublishDto) {
        String videoId = videoService.videoPublish(videoPublishDto);
        return R.ok(videoId);
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


    /**
     * 通过ids获取video集合
     * @param videoIds
     * @return
     */
    @GetMapping("{videoIds}")
    public R<List<Video>> queryVideoByVideoIds(@PathVariable("videoIds") List<String> videoIds) {
        return R.ok(videoService.queryVideoByVideoIds(videoIds));
    }

}

