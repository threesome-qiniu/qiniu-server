package com.qiniu.service.search.controller.v1;

import com.qiniu.common.domain.R;
import com.qiniu.service.search.service.VideoSearchService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * VideoSearchController
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
@RestController
@RequestMapping("/api/v1/video")
public class VideoSearchController {

    @Resource
    private VideoSearchService videoSearchService;

    @GetMapping()
    public R<?> searchVideo(@RequestParam("keyword")String keyword){
       return R.ok(videoSearchService.searchVideoFromES(keyword));
    }
}
