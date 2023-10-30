package com.qiniu.service.video.controller.v1;

import com.qiniu.model.video.domain.VideoUserComment;
import com.qiniu.service.video.service.IVideoUserCommentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (VideoUserComment)表控制层
 *
 * @author roydon
 * @since 2023-10-30 16:52:51
 */
@RestController
@RequestMapping("/api/v1")
public class VideoUserCommentController {

    @Resource
    private IVideoUserCommentService videoUserCommentService;


}

