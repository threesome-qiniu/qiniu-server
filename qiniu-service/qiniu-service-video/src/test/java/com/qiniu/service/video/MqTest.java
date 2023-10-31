package com.qiniu.service.video;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * MqTest
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
@Slf4j
@SpringBootTest
public class MqTest {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    void simpleQueue(){
        String queue = "simple.queue";
        rabbitTemplate.convertAndSend(queue, "hello,mq!");
    }

}
