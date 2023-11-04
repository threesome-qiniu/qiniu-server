package com.qiniu.service.video.service.impl;

import com.qiniu.common.utils.audit.SensitiveWordUtil;
import com.qiniu.common.utils.string.StringUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.common.context.UserContext;
import com.qiniu.common.exception.CustomException;
import com.qiniu.common.service.RedisService;
import com.qiniu.common.utils.bean.BeanCopyUtils;
import com.qiniu.common.utils.file.PathUtils;
import com.qiniu.common.utils.uniqueid.IdGenerator;
import com.qiniu.feign.behave.RemoteBehaveService;
import com.qiniu.feign.user.RemoteUserService;
import com.qiniu.model.search.vo.VideoSearchVO;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.video.domain.*;
import com.qiniu.model.video.dto.VideoFeedDTO;
import com.qiniu.model.video.dto.VideoPublishDto;
import com.qiniu.model.video.dto.VideoPageDto;
import com.qiniu.model.video.vo.VideoUploadVO;
import com.qiniu.model.video.vo.VideoVO;
import com.qiniu.service.video.constants.QiniuVideoOssConstants;
import com.qiniu.service.video.constants.VideoCacheConstants;
import com.qiniu.service.video.mapper.VideoMapper;
import com.qiniu.service.video.mapper.VideoUserCommentMapper;
import com.qiniu.service.video.service.IVideoCategoryRelationService;
import com.qiniu.service.video.service.IVideoSensitiveService;
import com.qiniu.service.video.service.IVideoService;
import com.qiniu.starter.file.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.qiniu.model.common.enums.HttpCodeEnum.*;
import static com.qiniu.model.video.mq.VideoDelayedQueueConstant.*;

/**
 * 视频表(Video)表服务实现类
 *
 * @author roydon
 * @since 2023-10-25 20:33:11
 */
@Slf4j
@Service("videoService")
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements IVideoService {

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private IVideoCategoryRelationService videoCategoryRelationService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RemoteUserService remoteUserService;

    @Resource
    private RedisService redisService;

    @Resource
    private IVideoSensitiveService videoSensitiveService;

    @Resource
    private RemoteBehaveService remoteBehaveService;

    @Override
    public VideoUploadVO uploadVideo(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        //对文件id进行判断，如果文件已经存在，则不上传，直接返回数据库中文件的存储路径
        String filePath = PathUtils.generateFilePath(originalFilename);
        VideoUploadVO videoUploadVO = new VideoUploadVO();
        videoUploadVO.setVideoUrl(fileStorageService.uploadVideo(file, QiniuVideoOssConstants.PREFIX_URL, filePath));
        videoUploadVO.setVframe(QiniuVideoOssConstants.PREFIX_URL + filePath + "?vframe/jpg/offset/1");
        return videoUploadVO;
    }

    @Override
    public Video selectById(String id) {
        Video video = videoMapper.selectById(id);
        return video;
    }

    @Transactional
    @Override
    public String videoPublish(VideoPublishDto videoPublishDto) {
        Long userId = UserContext.getUser().getUserId();
        //从数据库获得敏感信息
        //判断传过来的数据是否符合数据库字段标准
        if (videoPublishDto.getVideoTitle().length() > 30) {
            throw new CustomException(BIND_CONTENT_TITLE_FAIL);
        }
        if (videoPublishDto.getVideoDesc().length() > 200) {
            throw new CustomException(BIND_CONTENT_DESC_FAIL);
        }
        // 查出video敏感词表所有敏感词集合
        boolean b = sensitiveCheck(videoPublishDto.getVideoTitle() + videoPublishDto.getVideoDesc());
        if (b) {
            // 存在敏感词抛异常
            throw new CustomException(SENSITIVEWORD_ERROR);
        }
        //将传过来的数据拷贝到要存储的对象中
        Video video = BeanCopyUtils.copyBean(videoPublishDto, Video.class);
        //生成id
        String videoId = IdGenerator.generatorShortId();
        //向新的对象中封装信息
        video.setVideoId(videoId);
        video.setUserId(userId);
        video.setCreateTime(LocalDateTime.now());
        video.setCreateBy(userId.toString());
        video.setCoverImage(StringUtils.isNull(videoPublishDto.getCoverImage()) ?
                video.getVideoUrl() + VideoCacheConstants.VIDEO_VIEW_COVER_IMAGE_KEY : videoPublishDto.getCoverImage());
        //前端不传不用处理 将前端传递的分类拷贝到关联表对象
        if (StringUtils.isNotNull(videoPublishDto.getCategoryId())) {
            VideoCategoryRelation videoCategoryRelation = BeanCopyUtils.copyBean(videoPublishDto, VideoCategoryRelation.class);
            // video_id存入VideoCategoryRelation（视频分类关联表）
            videoCategoryRelation.setVideoId(video.getVideoId());
            // 再将videoCategoryRelation对象存入video_category_relation表中
            videoCategoryRelationService.saveVideoCategoryRelation(videoCategoryRelation);
        }
        // 将video对象存入video表中
        boolean save = this.save(video);
        if (save) {
            // 1.发送整个video对象发送消息，
            // 待添加视频封面
            VideoSearchVO videoSearchVO = new VideoSearchVO();
            videoSearchVO.setVideoId(video.getVideoId());
            videoSearchVO.setVideoTitle(video.getVideoTitle());
            // localdatetime转换为date
            videoSearchVO.setPublishTime(Date.from(video.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()));
            videoSearchVO.setCoverImage(video.getCoverImage());
            videoSearchVO.setVideoUrl(video.getVideoUrl());
            videoSearchVO.setUserId(userId);
            // 获取用户信息
            User userCache = redisService.getCacheObject("userinfo:" + userId);
            if (StringUtils.isNotNull(userCache)) {
                videoSearchVO.setUserNickName(userCache.getNickName());
                videoSearchVO.setUserAvatar(userCache.getAvatar());
            } else {
                User remoteUser = remoteUserService.userInfoById(userId).getData();
                videoSearchVO.setUserNickName(remoteUser.getNickName());
                videoSearchVO.setUserAvatar(remoteUser.getAvatar());
            }
            String msg = JSON.toJSONString(videoSearchVO);
            // 2.利用消息后置处理器添加消息头
            rabbitTemplate.convertAndSend(ESSYNC_DELAYED_EXCHANGE, ESSYNC_ROUTING_KEY, msg, message -> {
                // 3.添加延迟消息属性，设置1分钟
                message.getMessageProperties().setDelay(ESSYNC_DELAYED_TIME);
                return message;
            });
            log.debug(" ==> {} 发送了一条消息 ==> {}", ESSYNC_DELAYED_EXCHANGE, msg);
            return videoId;
        } else {
            throw new CustomException(null);
        }
    }

    /**
     * 敏感词检测
     */
    private boolean sensitiveCheck(String str) {
        LambdaQueryWrapper<VideoSensitive> userSensitiveLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userSensitiveLambdaQueryWrapper.select(VideoSensitive::getSensitives);
        List<String> videoSensitives = videoSensitiveService.list(userSensitiveLambdaQueryWrapper).stream().map(VideoSensitive::getSensitives).collect(Collectors.toList());
        SensitiveWordUtil.initMap(videoSensitives);
        //是否包含敏感词
        Map<String, Integer> map = SensitiveWordUtil.matchWords(str);
        // 存在敏感词
        return map.size() > 0;
    }

    @Override
    public IPage<Video> queryMyVideoPage(VideoPageDto pageDto) {
        Long userId = UserContext.getUser().getUserId();
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Video::getUserId, userId);
        queryWrapper.like(StringUtils.isNotEmpty(pageDto.getVideoTitle()), Video::getVideoTitle, pageDto.getVideoTitle());
        return this.page(new Page<>(pageDto.getPageNum(), pageDto.getPageSize()), queryWrapper);
    }

    @Override
    public IPage<Video> queryUserVideoPage(VideoPageDto pageDto) {
        if (StringUtils.isNull(pageDto.getUserId())) {
            return new Page<>();
        }
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Video::getUserId, pageDto.getUserId());
        queryWrapper.like(StringUtils.isNotEmpty(pageDto.getVideoTitle()), Video::getVideoTitle, pageDto.getVideoTitle());
        return this.page(new Page<>(pageDto.getPageNum(), pageDto.getPageSize()), queryWrapper);
    }

    /**
     * 视频feed接口
     *
     * @param videoFeedDTO createTime
     * @return video
     */
    @Override
    public List<VideoVO> feedVideo(VideoFeedDTO videoFeedDTO) {
        LocalDateTime createTime = videoFeedDTO.getCreateTime();
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        // 小于 createTime 的5条数据
        queryWrapper.lt(Video::getCreateTime, StringUtils.isNull(createTime) ? LocalDateTime.now() : createTime).orderByDesc(Video::getCreateTime).last("limit 5");
        List<Video> videoList;
        try {
            videoList = this.list(queryWrapper);
            if (StringUtils.isNull(videoList) || videoList.isEmpty()) {
                LambdaQueryWrapper<Video> queryWrapper2 = new LambdaQueryWrapper<>();
                // 小于 LocalDateTime.now() 的一条数据
                queryWrapper2.lt(Video::getCreateTime, LocalDateTime.now()).orderByDesc(Video::getCreateTime).last("limit 5");
                videoList = this.list(queryWrapper2);
            }
            // 浏览自增1存入redis
            videoList.forEach(v -> {
                viewNumIncrement(v.getVideoId());
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        List<VideoVO> videoVOList = new ArrayList<>();
        // 封装点赞数，观看量，评论量
        videoList.forEach(v -> {
            VideoVO videoVO = BeanCopyUtils.copyBean(v, VideoVO.class);
            Integer cacheLikeNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_LIKE_NUM_MAP_KEY, v.getVideoId());
            Integer cacheViewNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_VIEW_NUM_MAP_KEY, v.getVideoId());
            Integer cacheFavoriteNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_FAVORITE_NUM_MAP_KEY, v.getVideoId());
            videoVO.setLikeNum(StringUtils.isNull(cacheLikeNum) ? 0L : cacheLikeNum);
            videoVO.setViewNum(StringUtils.isNull(cacheViewNum) ? 0L : cacheViewNum);
            videoVO.setFavoritesNum(StringUtils.isNull(cacheFavoriteNum) ? 0L : cacheFavoriteNum);
            LambdaQueryWrapper<VideoUserComment> commentQW = new LambdaQueryWrapper<>();
            commentQW.eq(VideoUserComment::getVideoId, v.getVideoId());
            videoVO.setCommentNum(remoteBehaveService.getCommentCountByVideoId(videoVO.getVideoId()).getData());
            videoVOList.add(videoVO);
        });
        return videoVOList;
    }

    private void viewNumIncrement(String videoId) {
        redisService.incrementCacheMapValue(VideoCacheConstants.VIDEO_VIEW_NUM_MAP_KEY, videoId, 1);
    }

    /**
     * 根据ids查询视频
     *
     * @param videoIds
     * @return
     */
    @Override
    public List<Video> queryVideoByVideoIds(List<String> videoIds) {
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Video::getVideoId, videoIds);
        return this.list(queryWrapper);
    }
}
