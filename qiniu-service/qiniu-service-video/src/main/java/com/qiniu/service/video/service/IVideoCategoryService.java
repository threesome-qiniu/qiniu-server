package com.qiniu.service.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiniu.model.video.domain.VideoCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * (VideoCategory)表服务接口
 *
 * @author lzq
 * @since 2023-10-30 19:41:14
 */
public interface IVideoCategoryService extends IService<VideoCategory> {

    void saveVideoCategoriesToRedis();
}
