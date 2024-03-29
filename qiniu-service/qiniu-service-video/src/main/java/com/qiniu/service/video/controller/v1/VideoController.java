package com.qiniu.service.video.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qiniu.common.domain.R;
import com.qiniu.common.domain.vo.PageDataInfo;
import com.qiniu.common.service.RedisService;
import com.qiniu.common.utils.bean.BeanCopyUtils;
import com.qiniu.common.utils.string.StringUtils;
import com.qiniu.feign.user.RemoteUserService;
import com.qiniu.model.common.dto.PageDTO;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.dto.VideoPublishDto;
import com.qiniu.model.video.dto.VideoFeedDTO;
import com.qiniu.model.video.dto.VideoPageDto;
import com.qiniu.model.video.vo.VideoUploadVO;
import com.qiniu.model.video.vo.VideoVO;
import com.qiniu.service.video.constants.VideoCacheConstants;
import com.qiniu.service.video.service.IVideoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

    @Resource
    private RedisService redisService;

    @Resource
    private RemoteUserService remoteUserService;

    /**
     * 热门视频
     *
     * @param pageDTO
     * @return
     */
    @PostMapping("/hot")
    public PageDataInfo hotVideos(@RequestBody PageDTO pageDTO) {
        Integer startIndex = (pageDTO.getPageNum() - 1) * pageDTO.getPageSize();
        Integer endIndex = startIndex + pageDTO.getPageSize() - 1;
        Set videoIds = redisService.getCacheZSetRange(VideoCacheConstants.VIDEO_HOT, startIndex, endIndex);
        Long hotCount = redisService.getCacheZSetZCard(VideoCacheConstants.VIDEO_HOT);
        List<VideoVO> videoVOList = new ArrayList<>();
        videoIds.forEach(vid -> {
            Video video = videoService.selectById((String) vid);
            VideoVO videoVO = BeanCopyUtils.copyBean(video, VideoVO.class);
            User user = new User();
            try {
                user = remoteUserService.userInfoById(video.getUserId()).getData();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (StringUtils.isNotNull(user)) {
                videoVO.setUserNickName(user.getNickName());
                videoVO.setUserAvatar(user.getAvatar());
            }
            videoVO.setHotScore(redisService.getZSetScore(VideoCacheConstants.VIDEO_HOT, (String) vid));
            videoVOList.add(videoVO);
        });
        return PageDataInfo.genPageData(videoVOList, hotCount);
    }

    /**
     * 视频流接口 ,默认返回5条数据
     */
    @PostMapping("/feed")
    public R<List<VideoVO>> feed(@RequestBody VideoFeedDTO videoFeedDTO) {
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
        return R.ok(videoService.videoPublish(videoPublishDto));
    }

    /**
     * 分页查询我的视频
     *
     * @param pageDto
     * @return
     */
    @PostMapping("/mypage")
    public PageDataInfo myPage(@RequestBody VideoPageDto pageDto) {
        IPage<Video> videoIPage = videoService.queryMyVideoPage(pageDto);
        return PageDataInfo.genPageData(videoIPage.getRecords(), videoIPage.getTotal());
    }

    /**
     * 分页查询用户视频
     *
     * @param pageDto
     * @return
     */
    @PostMapping("/userpage")
    public PageDataInfo userPage(@RequestBody VideoPageDto pageDto) {
        IPage<Video> videoIPage = videoService.queryUserVideoPage(pageDto);
        return PageDataInfo.genPageData(videoIPage.getRecords(), videoIPage.getTotal());
    }

    /**
     * 通过ids获取video集合
     *
     * @param videoIds
     * @return
     */
    @GetMapping("{videoIds}")
    public R<List<Video>> queryVideoByVideoIds(@PathVariable("videoIds") List<String> videoIds) {
        return R.ok(videoService.queryVideoByVideoIds(videoIds));
    }

    @DeleteMapping("/{videoId}")
    public R<?> deleteVideoByVideoIds(@PathVariable("videoId") String videoId) {
        videoService.deleteVideoByVideoIds(videoId);
        return null;
    }

}

