package com.qiniu.model.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.mapper.VideoMapper;
import com.qiniu.model.video.service.IVideoService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * 视频表(Video)表服务实现类
 *
 * @author roydon
 * @since 2023-10-25 20:33:11
 */
@Service("videoService")
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements IVideoService {
    @Resource
    private VideoMapper videoMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param videoId 主键
     * @return 实例对象
     */
    @Override
    public Video queryById(String videoId) {
        return this.videoMapper.queryById(videoId);
    }

}
