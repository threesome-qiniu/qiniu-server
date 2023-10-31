package com.qiniu.service.search.service;

import com.qiniu.model.video.domain.Video;

/**
 * VideoSyncEsService
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
public interface VideoSearchService {

    /**
     * 视频同步到es
     *
     * @param video
     */
    void videoSync(Video video);

}
