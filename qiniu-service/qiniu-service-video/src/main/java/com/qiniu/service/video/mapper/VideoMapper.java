package com.qiniu.service.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiniu.model.video.domain.Video;
import org.apache.ibatis.annotations.Mapper;

/**
 * 视频表(Video)表数据库访问层
 *
 * @author roydon
 * @since 2023-10-25 20:33:09
 */
@Mapper
public interface VideoMapper extends BaseMapper<Video>{

    String getVideoUrlByVideoId(String videoId);

}

