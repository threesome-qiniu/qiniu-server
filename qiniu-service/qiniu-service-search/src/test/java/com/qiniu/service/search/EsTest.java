package com.qiniu.service.search;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

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
