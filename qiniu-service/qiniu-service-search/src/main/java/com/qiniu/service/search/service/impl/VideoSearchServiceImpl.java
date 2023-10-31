package com.qiniu.service.search.service.impl;

import com.qiniu.model.video.domain.Video;
import com.qiniu.service.search.service.VideoSearchService;
import org.springframework.stereotype.Service;

/**
 * VideoSearchServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
@Service("videoSearchServiceImpl")
public class VideoSearchServiceImpl implements VideoSearchService {

    /**
     * 视频同步到es
     *
     * @param video
     */
    @Override
    public void videoSync(Video video) {

    }
}
