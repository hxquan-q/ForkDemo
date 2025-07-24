package com.hxq.job.handler;

import com.hxq.common.feign.ProductFeignClient;
import com.hxq.common.response.Result;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 商品定时任务处理器
 */
@Component
@Slf4j
public class ProductJobHandler {

    @Resource
    private ProductFeignClient productFeignClient;

    /**
     * 刷新商品库存到Redis
     * 每小时执行一次
     */
    @XxlJob("refreshProductStockJobHandler")
    @CircuitBreaker(name = "refreshProductStock", fallbackMethod = "refreshProductStockFallback")
    public void refreshProductStock() {
        XxlJobHelper.log("开始刷新商品库存到Redis");
        log.info("开始刷新商品库存到Redis");

        try {
            // 调用商品服务刷新库存
            Result<Void> result = productFeignClient.initProductStockToRedis();
            if (result.getSuccess()) {
                XxlJobHelper.log("刷新商品库存到Redis成功");
                log.info("刷新商品库存到Redis成功");
            } else {
                XxlJobHelper.log("刷新商品库存到Redis失败: " + result.getMessage());
                log.error("刷新商品库存到Redis失败: {}", result.getMessage());
            }
        } catch (Exception e) {
            XxlJobHelper.log("刷新商品库存到Redis异常: " + e.getMessage());
            log.error("刷新商品库存到Redis异常", e);
            throw e;
        }
    }

    /**
     * 刷新商品库存到Redis的熔断降级处理方法
     */
    public void refreshProductStockFallback(Throwable throwable) {
        XxlJobHelper.log("刷新商品库存到Redis熔断: " + throwable.getMessage());
        log.error("刷新商品库存到Redis熔断", throwable);
    }
}