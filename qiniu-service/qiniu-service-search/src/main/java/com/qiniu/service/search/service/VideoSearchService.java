package com.qiniu.service.search.service;

import com.qiniu.model.video.domain.Video;

import java.util.List;

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
     * @param json videoSearchVO json
     */
    void videoSync(String json);

    /**
     * es搜索视频
     *
     * @param keyword
     * @return
     */
    List<Video> searchVideoFromES(String keyword);

}
