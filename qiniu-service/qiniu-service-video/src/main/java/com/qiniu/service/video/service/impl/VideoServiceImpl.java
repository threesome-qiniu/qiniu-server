package com.qiniu.service.video.service.impl;

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
import com.qiniu.feign.user.RemoteUserService;
import com.qiniu.model.search.vo.VideoSearchVO;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.video.domain.*;
import com.qiniu.model.video.dto.VideoFeedDTO;
import com.qiniu.model.video.dto.VideoBindDto;
import com.qiniu.model.video.dto.VideoPageDto;
import com.qiniu.model.video.vo.VideoUserLikeAndFavoriteVo;
import com.qiniu.model.video.vo.VideoVO;
import com.qiniu.service.video.constants.QiniuVideoOssConstants;
import com.qiniu.service.video.constants.VideoCacheConstants;
import com.qiniu.service.video.mapper.VideoCategoryMapper;
import com.qiniu.service.video.mapper.VideoMapper;
import com.qiniu.service.video.mapper.VideoSensitiveMapper;
import com.qiniu.service.video.mapper.VideoUserCommentMapper;
import com.qiniu.service.video.service.IVideoCategoryRelationService;
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
    private VideoCategoryMapper videoCategoryMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RemoteUserService remoteUserService;

    @Resource
    private RedisService redisService;

    @Resource
    private VideoUserCommentMapper videoUserCommentMapper;

    @Resource
    private VideoSensitiveMapper videoSensitiveMapper;


    @Override
    public String uploadVideo(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        //对文件id进行判断，如果文件已经存在，则不上传，直接返回数据库中文件的存储路径
        String filePath = PathUtils.generateFilePath(originalFilename);
        return fileStorageService.uploadVideo(file, QiniuVideoOssConstants.PREFIX_URL, filePath);
    }

    @Override
    public Video selectById(String id) {
        Video video = videoMapper.selectById(id);
        return video;
    }

    @Transactional
    @Override
    public String videoPublish(VideoBindDto videoBindDto) {
        Long userId = UserContext.getUser().getUserId();
        //从数据库获得敏感信息
        //判断传过来的数据是否符合数据库字段标准
        if (videoBindDto.getVideoTitle().length() > 30) {
            throw new CustomException(BIND_CONTENT_TITLE_FAIL);
        }
        if (videoBindDto.getVideoDesc().length() > 200) {
            throw new CustomException(BIND_CONTENT_DESC_FAIL);
        }
        //构建敏感词查询条件
        LambdaQueryWrapper<VideoSensitive> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(VideoSensitive::getId)
                .like(VideoSensitive::getSensitives, videoBindDto.getVideoTitle())
                .or(w -> w.like(VideoSensitive::getSensitives, videoBindDto.getVideoDesc()));
        List<VideoSensitive> videoSensitives = videoSensitiveMapper.selectList(queryWrapper);
        //如果结果不为空，证明有敏感词，提示异常
        if (!videoSensitives.isEmpty()) {
            throw new CustomException(SENSITIVEWORD_ERROR);
        }
        //将传过来的数据拷贝到要存储的对象中
        Video video = BeanCopyUtils.copyBean(videoBindDto, Video.class);
        //生成id
        String videoId = IdGenerator.generatorShortId();
        //向新的对象中封装信息
        video.setVideoId(videoId);
        video.setUserId(userId);
        video.setCreateTime(LocalDateTime.now());
        video.setCreateBy(userId.toString());
        //将前端传递的分类拷贝到关联表对象
        VideoCategoryRelation videoCategoryRelation = BeanCopyUtils.copyBean(videoBindDto, VideoCategoryRelation.class);
        //video_id存入VideoCategoryRelation（视频分类关联表）
        videoCategoryRelation.setVideoId(video.getVideoId());
        //先将video对象存入video表中
        boolean save = this.save(video);
        //再将videoCategoryRelation对象存入video_category_relation表中
        videoCategoryRelationService.saveVideoCategoryRelation(videoCategoryRelation);
        if (save) {
            // 1.发送整个video对象发送消息，
            // TODO 待添加视频封面
            VideoSearchVO videoSearchVO = new VideoSearchVO();
            videoSearchVO.setVideoId(video.getVideoId());
            videoSearchVO.setVideoTitle(video.getVideoTitle());
            // localdatetime转换为date
            videoSearchVO.setPublishTime(Date.from(video.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()));
            videoSearchVO.setCoverImage("null");
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
    public VideoVO feedVideo(VideoFeedDTO videoFeedDTO) {
        LocalDateTime createTime = videoFeedDTO.getCreateTime();
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        // 小于等于createTime的一条数据
        queryWrapper.lt(Video::getCreateTime, StringUtils.isNull(createTime) ? LocalDateTime.now() : createTime).orderByDesc(Video::getCreateTime).last("limit 1");
        Video one;
        try {
            one = getOne(queryWrapper);
            // TODO 浏览自增1存入redis
            viewNumIncrement(one.getVideoId());
            if (StringUtils.isNull(one)) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        // TODO 封装点赞数，观看量，评论量
        VideoVO videoVO = BeanCopyUtils.copyBean(one, VideoVO.class);
        int cacheLikeNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_LIKE_NUM_MAP_KEY, one.getVideoId());
        int cacheViewNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_VIEW_NUM_MAP_KEY, one.getVideoId());
        int cacheFavoriteNum = redisService.getCacheMapValue(VideoCacheConstants.VIDEO_FAVORITE_NUM_MAP_KEY, one.getVideoId());
        videoVO.setLikeNum(Long.valueOf(cacheLikeNum));
        videoVO.setViewNum(Long.valueOf(cacheViewNum));
        videoVO.setFavoritesNum(Long.valueOf(cacheFavoriteNum));
        LambdaQueryWrapper<VideoUserComment> commentQW = new LambdaQueryWrapper<>();
        commentQW.eq(VideoUserComment::getVideoId, one.getVideoId());
        List<VideoUserComment> videoUserComments = videoUserCommentMapper.selectList(commentQW);
        videoVO.setCommentNum((long) videoUserComments.size());
        return videoVO;
    }

    //    @PostConstruct
//    public void init() {
//        log.info("新闻浏览量写入缓存开始==>");
//        List<AppNews> appNewsList = list();
//        Map<String, Integer> newsViewMap = appNewsList.stream().collect(Collectors.toMap(AppNews::getNewsId, AppNews::getViewNum));
//        redisCache.setCacheMap(CacheConstants.NEWS_VIEW_NUM_KEY, newsViewMap);
//        log.info("<==新闻浏览量写入缓存成功");
//    }

    /**
     * 分页查询用户的点赞列表
     *
     * @param pageDto
     * @return
     */
    @Override
    public List<VideoUserLikeAndFavoriteVo> queryMyLikeVideoPage(VideoPageDto pageDto) {
        Long userId = UserContext.getUser().getUserId();
        List<Video> userLikedVideos = videoMapper.getUserLikesVideos(userId, (pageDto.getPageNum() - 1) * pageDto.getPageSize(), pageDto.getPageSize());
        ArrayList<VideoUserLikeAndFavoriteVo> objects = new ArrayList<>();
        for (Video userLikedVideo : userLikedVideos) {
            objects.add(BeanCopyUtils.copyBean(userLikedVideo, VideoUserLikeAndFavoriteVo.class));
        }
        return objects;
    }

    /**
     * 分页查询用户的点赞列表
     *
     * @param pageDto
     * @return
     */
    @Override
    public List<VideoUserLikeAndFavoriteVo> queryMyFavoritesVideoPage(VideoPageDto pageDto) {
        Long userId = UserContext.getUser().getUserId();
        List<Video> userLikedVideos = videoMapper.getUserFavoritesVideos(userId, (pageDto.getPageNum() - 1) * pageDto.getPageSize(), pageDto.getPageSize());
        ArrayList<VideoUserLikeAndFavoriteVo> objects = new ArrayList<>();
        for (Video userLikedVideo : userLikedVideos) {
            objects.add(BeanCopyUtils.copyBean(userLikedVideo, VideoUserLikeAndFavoriteVo.class));
        }
        return objects;
    }

    private void viewNumIncrement(String videoId) {
        redisService.incrementCacheMapValue(VideoCacheConstants.VIDEO_VIEW_NUM_MAP_KEY, videoId, 1);
    }

}
