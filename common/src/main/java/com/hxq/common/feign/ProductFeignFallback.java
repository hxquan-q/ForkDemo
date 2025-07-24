package com.hxq.common.feign;

import com.hxq.common.entity.Product;
import com.hxq.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 商品服务Feign降级处理
 */
@Slf4j
@Component
public class ProductFeignFallback implements ProductFeignClient {

    @Override
    public Result<Product> getProductById(Long id) {
        log.error("获取商品信息失败，商品ID: {}", id);
        return Result.failure("获取商品信息失败，服务降级");
    }

    @Override
    public Result<Void> deductStock(Long productId, Integer quantity) {
        log.error("扣减库存失败，商品ID: {}, 数量: {}", productId, quantity);
        return Result.failure("扣减库存失败，服务降级");
    }

    @Override
    public Result<Void> restoreStock(Long productId, Integer quantity) {
        log.error("恢复库存失败，商品ID: {}, 数量: {}", productId, quantity);
        return Result.failure("恢复库存失败，服务降级");
    }

    @Override
    public Result<Void> initProductStockToRedis() {
        log.error("初始化商品库存到Redis失败，服务降级");
        return Result.failure("初始化商品库存到Redis失败，服务降级");
    }
}