package com.qiniu.service.search;

import com.alibaba.fastjson.JSON;
import com.qiniu.model.search.vo.VideoSearchVO;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * EsTest
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
@SpringBootTest
public class EsTest {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Test
    void testCreateIndex(){

    }

}
