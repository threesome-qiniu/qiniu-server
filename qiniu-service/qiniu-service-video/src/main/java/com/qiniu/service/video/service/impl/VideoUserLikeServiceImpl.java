package com.qiniu.service.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.common.constant.UserConstants;
import com.qiniu.common.context.UserContext;
import com.qiniu.common.service.RedisService;
import com.qiniu.common.utils.string.StringUtils;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.domain.VideoUserLike;
import com.qiniu.model.video.vo.VideoUserLikeVo;
import com.qiniu.service.video.constants.VideoCacheConstants;
import com.qiniu.service.video.mapper.VideoMapper;
import com.qiniu.service.video.mapper.VideoUserLikeMapper;
import com.qiniu.service.video.service.IVideoUserLikeService;
import kotlin.collections.ArrayDeque;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 点赞表(VideoUserLike)表服务实现类
 *
 * @author lzq
 * @since 2023-10-30 14:33:01
 */
@Slf4j
@Service("videoUserLikeService")
public class VideoUserLikeServiceImpl extends ServiceImpl<VideoUserLikeMapper, VideoUserLike> implements IVideoUserLikeService {

    @Resource
    private RedisService redisService;

    @Autowired
    private VideoMapper videoMapper;

    /**
     * 向视频点赞表插入点赞信息
     *
     * @param videoId
     * @return
     */
    @Override
    public boolean videoLike(String videoId) {
        Long userId = UserContext.getUser().getUserId();
        LambdaQueryWrapper<VideoUserLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserLike::getVideoId, videoId).eq(VideoUserLike::getUserId, userId);
        List<VideoUserLike> list = this.list(queryWrapper);
        if (StringUtils.isNull(list) || list.isEmpty()) {
            VideoUserLike videoUserLike = new VideoUserLike();
            videoUserLike.setVideoId(videoId);
            videoUserLike.setUserId(userId);
            videoUserLike.setCreateTime(LocalDateTime.now());
            //将本条点赞信息存储到redis
            likeNumIncrease(videoId);
            return this.save(videoUserLike);
        } else {
            //将本条点赞信息从redis
            likeNumDecrease(videoId);
            return this.remove(queryWrapper);
        }
    }

    /**
     * 用户查询自己点赞过的视频（收藏列表）
     * @param userId
     * @return
     */
    @Override
    public List<VideoUserLikeVo> userLikes(Long userId) {
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Video::getUserId, userId);
        List<VideoUserLikeVo> videoUserLikeVos = new ArrayList<>();
        List<Video> list = videoMapper.selectList(queryWrapper);
        for (Video video : list) {
            VideoUserLikeVo videoUserLikeVo = new VideoUserLikeVo();
            BeanUtils.copyProperties(video, videoUserLikeVo );
            videoUserLikeVos.add(videoUserLikeVo);
        }
        return videoUserLikeVos;
    }

    /**
     * 缓存中点赞量自增一
     * @param videoId
     */
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
