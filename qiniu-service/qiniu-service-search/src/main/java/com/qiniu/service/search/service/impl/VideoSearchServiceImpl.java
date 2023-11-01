package com.qiniu.service.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.qiniu.model.search.vo.VideoSearchVO;
import com.qiniu.model.video.domain.Video;
import com.qiniu.service.search.service.VideoSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * VideoSearchServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
@Slf4j
@Service("videoSearchServiceImpl")
public class VideoSearchServiceImpl implements VideoSearchService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 视频同步到es
     */
    @Override
    public void videoSync(String json) {
        VideoSearchVO videoSearchVO = JSON.parseObject(json, VideoSearchVO.class);
        IndexRequest indexRequest = new IndexRequest("search_video");
        indexRequest.id(videoSearchVO.getVideoId());
        indexRequest.source(json, XContentType.JSON);
        try {
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("sync es error ==> {}", e.getMessage());
        }
    }

    /**
     * es搜索视频
     *
     * @param keyword
     * @return
     */
    @Override
    public List<Video> searchVideoFromES(String keyword) {
        return null;
    }
}
