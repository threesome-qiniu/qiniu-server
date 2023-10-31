package com.qiniu.service.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.common.context.UserContext;
import com.qiniu.common.service.RedisService;
import com.qiniu.common.utils.string.StringUtils;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.domain.VideoUserFavorites;
import com.qiniu.model.video.vo.VideoUserVo;
import com.qiniu.service.video.constants.VideoCacheConstants;
import com.qiniu.service.video.mapper.VideoMapper;
import com.qiniu.service.video.mapper.VideoUserFavoritesMapper;
import com.qiniu.service.video.service.IVideoUserFavoritesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频收藏表(VideoUserFavorites)表服务实现类
 *
 * @author lzq
 * @since 2023-10-31 15:57:38
 */
@Service("videoUserFavoritesService")
public class VideoUserFavoritesServiceImpl extends ServiceImpl<VideoUserFavoritesMapper, VideoUserFavorites> implements IVideoUserFavoritesService {
    @Resource
    private VideoUserFavoritesMapper videoUserFavoritesMapper;

    @Resource
    private RedisService redisService;

    @Autowired
    private VideoMapper videoMapper;

    /**
     * 用户收藏
     *
     * @param videoId
     * @return
     */
    @Override
    public boolean videoFavorites(String videoId) {
        Long userId = UserContext.getUser().getUserId();
        //从数据库中获得视频链接
        String videoUrl = videoMapper.getVideoUrlByVideoId(videoId);
        LambdaQueryWrapper<VideoUserFavorites> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VideoUserFavorites::getVideoId, videoId).eq(VideoUserFavorites::getUserId, userId);
        List<VideoUserFavorites> list = this.list(queryWrapper);
        if (StringUtils.isNull(list) || list.isEmpty()) {
            VideoUserFavorites videoUserFavorites = new VideoUserFavorites();
            videoUserFavorites.setVideoId(videoId);
            videoUserFavorites.setUserId(userId);
            videoUserFavorites.setCreateTime(LocalDateTime.now());
            //将本条点赞信息存储到redis（key为videoId,value为videoUrl）
            likeNumIncrease(videoId, videoUrl);
            return this.save(videoUserFavorites);
        } else {
            //将本条点赞信息从redis
            likeNumDecrease(videoId, videoUrl);
            return this.remove(queryWrapper);
        }
    }

    /**
     * 获取用户的收藏列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<VideoUserVo> userFavorites(Long userId) {
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Video::getUserId, userId);
        List<VideoUserVo> videoUserVos = new ArrayList<>();
        List<Video> list = videoMapper.selectList(queryWrapper);
        for (Video video : list) {
            VideoUserVo videoUserVo = new VideoUserVo();
            BeanUtils.copyProperties(video, videoUserVo);
            videoUserVos.add(videoUserVo);
        }
        return videoUserVos;
    }

    public void likeNumIncrease(String videoId, String videoUrl) {
        // 缓存中点赞量自增一
        redisService.incrementCacheMapValue(VideoCacheConstants.VIDEO_FAVORITY_NUM_KEY + videoId, videoUrl, 1);
    }

    /**
     * 缓存中点赞量自增一
     *
     * @param videoId
     */
    public void likeNumDecrease(String videoId, String videoUrl) {
        // 缓存中阅读量自增一
        redisService.incrementCacheMapValue(VideoCacheConstants.VIDEO_FAVORITY_NUM_KEY + videoId, videoUrl, -1);
    }
}
