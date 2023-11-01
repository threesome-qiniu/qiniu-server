package com.qiniu.service.search.service;

import com.qiniu.model.search.dto.VideoSearchKeywordDTO;
import com.qiniu.service.search.domain.VideoSearchVO;

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
     * es分页搜索视频
     *
     * @param dto
     */
    List<VideoSearchVO> searchVideoFromES(VideoSearchKeywordDTO dto) throws Exception;

}
