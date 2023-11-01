package com.qiniu.service.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.qiniu.common.service.RedisService;
import com.qiniu.common.utils.bean.BeanCopyUtils;
import com.qiniu.model.video.domain.VideoCategory;
import com.qiniu.model.video.vo.VideoCategoryVo;
import com.qiniu.service.video.constants.VideoCacheConstants;
import com.qiniu.service.video.mapper.VideoCategoryMapper;
import com.qiniu.service.video.service.IVideoCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * (VideoCategory)表服务实现类
 *
 * @author lzq
 * @since 2023-10-30 19:41:14
 */
@Slf4j
@Service("videoCategoryService")
public class VideoCategoryServiceImpl extends ServiceImpl<VideoCategoryMapper, VideoCategory> implements IVideoCategoryService {
    @Resource
    private VideoCategoryMapper videoCategoryMapper;

    @Autowired
    RedisService redisService;

    @Override
    public List<VideoCategory> saveVideoCategoriesToRedis() {
        // 查询数据库获取视频分类列表
        List<VideoCategory> videoCategories = videoCategoryMapper.getAllVideoCategory();
        if (videoCategories.isEmpty()) {
            return new ArrayList<>();
        }
        redisService.setCacheList(VideoCacheConstants.VIDEO_CATEGORY_PREFIX, videoCategories);
        return videoCategories;
    }

    /**
     * 获取所有的分类列表
     */
    @Override
    public List<VideoCategoryVo> selectAllCategory() {

        List<VideoCategory> cacheList = redisService.getCacheList(VideoCacheConstants.VIDEO_CATEGORY_PREFIX);
        if (cacheList.isEmpty()) {
            cacheList = saveVideoCategoriesToRedis();
        }
        List<VideoCategoryVo> videoCategoryVos = BeanCopyUtils.copyBeanList(cacheList, VideoCategoryVo.class);
        return videoCategoryVos;
    }

}
