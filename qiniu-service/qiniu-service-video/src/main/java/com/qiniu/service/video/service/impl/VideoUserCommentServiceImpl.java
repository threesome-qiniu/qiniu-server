package com.qiniu.service.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.model.video.domain.VideoUserComment;
import com.qiniu.service.video.mapper.VideoUserCommentMapper;
import com.qiniu.service.video.service.IVideoUserCommentService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;

/**
 * (VideoUserComment)表服务实现类
 *
 * @author roydon
 * @since 2023-10-30 16:52:53
 */
@Service("videoUserCommentService")
public class VideoUserCommentServiceImpl extends ServiceImpl<VideoUserCommentMapper, VideoUserComment> implements IVideoUserCommentService {
    @Resource
    private VideoUserCommentMapper videoUserCommentMapper;

}
