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
import com.qiniu.service.video.constants.VideoCacheConstants;
import com.qiniu.service.video.mapper.VideoMapper;
import com.qiniu.service.video.mapper.VideoUserLikeMapper;
import com.qiniu.service.video.service.IVideoUserLikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
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

    /**
     * 向视频点赞表插入点赞信息
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
           return this.save(videoUserLike);
        }else {
           return this.remove(queryWrapper);
        }
    }

//    @PostConstruct
//    public void init() {
//        log.info(" 视频点赞量写入缓存开始==>");
//        List<VideoUserLike> videoUserLikeList = list();
//        Map<String, Long> videoUserLikeMap = videoUserLikeList.stream().collect(Collectors.toMap(VideoUserLike::getVideoId, VideoUserLike::getId));
//        redisService.setCacheMap(VideoCacheConstants.VIDEO_INFO_PREFIX, videoUserLikeMap);
//
//        log.info("<==视频点赞量写入缓存成功");
//    }
//



}
