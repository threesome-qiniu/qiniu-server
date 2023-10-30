package com.qiniu.service.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.qiniu.common.service.RedisService;
import com.qiniu.model.video.domain.VideoCategory;
import com.qiniu.service.video.mapper.VideoCategoryMapper;
import com.qiniu.service.video.service.IVideoCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.List;

/**
 * (VideoCategory)表服务实现类
 *
 * @author lzq
 * @since 2023-10-30 19:41:14
 */
@Service("videoCategoryService")
public class VideoCategoryServiceImpl extends ServiceImpl<VideoCategoryMapper, VideoCategory> implements IVideoCategoryService {
    @Resource
    private VideoCategoryMapper videoCategoryMapper;


    @Autowired
    RedisService redisService;

    @Override
    public void saveVideoCategoriesToRedis() {
        // 查询数据库获取视频分类列表
        List<VideoCategory> videoCategories = findAllVideoCategoriesFromDatabase();
        if (videoCategories.isEmpty()){
            System.out.println("数据库数据为空");
        }

        // 将视频分类列表转换为JSON字符串
        String json = convertListToJson(videoCategories);

        // 将JSON字符串存储到Redis中，键为"video_categories"
        redisService.setCacheObject("video_category",json);
        System.out.println("成功存入缓存");
    }

    private List<VideoCategory> findAllVideoCategoriesFromDatabase() {
        // 在这里实现从数据库查询视频分类的逻辑
        List<VideoCategory> allVideoCategory = videoCategoryMapper.getAllVideoCategory();
        // ...
        return allVideoCategory;
    }

    private String convertListToJson(List<VideoCategory> list) {
        // 在这里实现将视频分类列表转换为JSON字符串的逻辑
        Gson gson = new Gson();
        String json = gson.toJson(list);
        // ...
        return json;
    }
}
