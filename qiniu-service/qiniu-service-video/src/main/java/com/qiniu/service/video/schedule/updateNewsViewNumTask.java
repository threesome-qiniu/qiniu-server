package com.qiniu.service.video.schedule;

import com.qiniu.common.service.RedisService;
import com.qiniu.model.video.domain.Video;
import com.qiniu.service.video.constants.VideoCacheConstants;
import com.qiniu.service.video.service.IVideoService;
import com.qiniu.service.video.service.IVideoUserLikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/10/31 10:58
 */
@Component
public class updateNewsViewNumTask {

    private static final Logger log = LoggerFactory.getLogger(updateNewsViewNumTask.class);

    @Resource
    private RedisService redisService;

    @Resource
    private IVideoService videoService;

    @Scheduled(fixedDelay = 1000*60)
    public void updateLikeCount() {
        log.info("开始从redis更新视频点赞量==>");
        //获取redis中的浏览量
        Map<String, Integer> viewNumMap = redisService.getCacheMap(VideoCacheConstants.VIDEO_LIKE_NUM_KEY);
        List<Video> newsList = viewNumMap.entrySet().stream().map(entry -> {
            Video an = new Video();
            an.setVideoId(entry.getKey());
            an.setLikeNum(Long.valueOf(entry.getValue()));
            return an;
        }).collect(Collectors.toList());
        //更新数据库
        videoService.updateBatchById(newsList);
        log.info("<==视频点赞量数据库与redis同步成功");
    }

    @Scheduled(fixedDelay = 1000*60)
    public void updateFavorityCount() {
        log.info("开始从redis更新视频收藏量==>");
        //获取redis中的浏览量
        Map<String, Integer> viewNumMap = redisService.getCacheMap(VideoCacheConstants.VIDEO_FAVORITY_NUM_KEY);
        List<Video> newsList = viewNumMap.entrySet().stream().map(entry -> {
            Video an = new Video();
            an.setVideoId(entry.getKey());
            an.setLikeNum(Long.valueOf(entry.getValue()));
            return an;
        }).collect(Collectors.toList());
        //更新数据库
        videoService.updateBatchById(newsList);
        log.info("<==视频收藏量数据库与redis同步成功");
    }
}
