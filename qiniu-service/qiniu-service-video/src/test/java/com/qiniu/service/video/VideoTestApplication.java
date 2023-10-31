package com.qiniu.service.video;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qiniu.common.context.UserContext;
import com.qiniu.common.service.RedisService;
import com.qiniu.common.utils.string.StringUtils;
import com.qiniu.feign.user.RemoteUserService;
import com.qiniu.model.video.domain.VideoUserLike;
import com.qiniu.model.video.vo.VideoUserLikeVo;
import com.qiniu.service.video.constants.VideoCacheConstants;
import com.qiniu.service.video.service.IVideoCategoryService;
import com.qiniu.service.video.service.IVideoService;
import com.qiniu.service.video.service.IVideoUserLikeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/10/29 16:07
 */
@SpringBootTest
public class VideoTestApplication {

    @Autowired
    IVideoService videoService;

    @Autowired
    private IVideoCategoryService videoCategoryService;

    @Autowired
    private IVideoUserLikeService videoUserLikeService;

    @Autowired
    private RedisService redisService;

    @Resource
    private RemoteUserService remoteUserService;

//    void bindTest(){
//        VideoBindDto videoBindDto = new VideoBindDto();
//        videoBindDto.setVideoId(1);
//        videoService.bindVideoAndUser();
//    }

    @Test
    void getUser() {
        videoCategoryService.saveVideoCategoriesToRedis();

    }

    @Test
    void userLikesTest(){
        List<VideoUserLikeVo> videoUserLikeVos = videoUserLikeService.userLikes(3L);
        System.out.println(videoUserLikeVos.size());
    }

    @Test
    void videoLikeTest(){
        String videoId="11685954002238832647a8379a1";
        Long userId = 2L;
        LambdaQueryWrapper<VideoUserLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserLike::getVideoId, videoId).eq(VideoUserLike::getUserId, userId);
        List<VideoUserLike> list = videoUserLikeService.list(queryWrapper);
        if (StringUtils.isNull(list) || list.isEmpty()) {
            VideoUserLike videoUserLike = new VideoUserLike();
            videoUserLike.setVideoId(videoId);
            videoUserLike.setUserId(userId);
            videoUserLike.setCreateTime(LocalDateTime.now());
            //将本条点赞信息存储到redis
            likeNumIncrease(videoId);
            videoUserLikeService.save(videoUserLike);
        } else {
            //将本条点赞信息从redis
            likeNumDecrease(videoId);
           videoUserLikeService.remove(queryWrapper);
        }


    }

    public void likeNumIncrease(String videoId) {
        // 缓存中点赞量自增一
        redisService.incrementCacheMapValue(VideoCacheConstants.VIDEO_LIKE_NUM_KEY, videoId, 1);
    }

    /**
     * 缓存中点赞量自增一
     * @param videoId
     */
    public void likeNumDecrease(String videoId) {
        // 缓存中阅读量自增一
        redisService.incrementCacheMapValue(VideoCacheConstants.VIDEO_LIKE_NUM_KEY, videoId, -1);
    }

}
