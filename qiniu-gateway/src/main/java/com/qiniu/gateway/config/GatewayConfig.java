package com.qiniu.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.qiniu.gateway.constant.ServiceNameConstants;
import com.qiniu.gateway.handler.SentinelFallbackHandler;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * 限流规则配置类
 */
@Configuration
public class GatewayConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelFallbackHandler sentinelGatewayExceptionHandler() {
        return new SentinelFallbackHandler();
    }

    @Bean
    @Order(-1)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    @PostConstruct
    public void doInit() {
        // 加载网关限流规则
        initGatewayRules();
    }

    /**
     * 网关限流规则
     */
    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        rules.add(new GatewayFlowRule(ServiceNameConstants.USER_SERVICE)
                .setCount(1)    // 限流阈值
                .setIntervalSec(1));   // 统计时间窗口，单位是秒，默认是 1 秒
        rules.add(new GatewayFlowRule(ServiceNameConstants.VIDEO_SERVICE)
                .setCount(10)    // 限流阈值
                .setIntervalSec(1));
        // 加载网关限流规则
        GatewayRuleManager.loadRules(rules);
    }
}
