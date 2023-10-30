package com.qiniu.service.video.controller.v1;

import com.qiniu.model.video.domain.VideoCategory;
import com.qiniu.service.video.service.IVideoCategoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (VideoCategory)表控制层
 *
 * @author lzq
 * @since 2023-10-30 19:41:13
 */
@RestController
@RequestMapping("/videoCategory")
public class VideoCategoryController {
    
    @Resource
    private IVideoCategoryService videoCategoryService;


}

