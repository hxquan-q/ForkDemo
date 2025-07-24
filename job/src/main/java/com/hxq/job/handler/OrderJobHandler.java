package com.hxq.job.handler;

import com.hxq.common.feign.OrderFeignClient;
import com.hxq.common.response.Result;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单定时任务处理器
 */
@Component
@Slf4j
public class OrderJobHandler {

    @Resource
    private OrderFeignClient orderFeignClient;

    /**
     * 处理超时未支付的订单
     * 每分钟执行一次
     */
    @XxlJob("orderTimeoutJobHandler")
    @CircuitBreaker(name = "handleTimeoutOrders", fallbackMethod = "handleTimeoutOrdersFallback")
    public void handleTimeoutOrders() {
        XxlJobHelper.log("开始处理超时未支付订单");
        log.info("开始处理超时未支付订单");

        try {
            // 调用订单服务处理超时订单
            Result<Void> result = orderFeignClient.handleTimeoutOrders();
            if (result.getSuccess()) {
                XxlJobHelper.log("处理超时未支付订单成功");
                log.info("处理超时未支付订单成功");
            } else {
                XxlJobHelper.log("处理超时未支付订单失败: " + result.getMessage());
                log.error("处理超时未支付订单失败: {}", result.getMessage());
            }
        } catch (Exception e) {
            XxlJobHelper.log("处理超时未支付订单异常: " + e.getMessage());
            log.error("处理超时未支付订单异常", e);
            throw e;
        }
    }

    /**
     * 处理超时未支付订单的熔断降级处理方法
     */
    public void handleTimeoutOrdersFallback(Throwable throwable) {
        XxlJobHelper.log("处理超时未支付订单熔断: " + throwable.getMessage());
        log.error("处理超时未支付订单熔断", throwable);
    }
}