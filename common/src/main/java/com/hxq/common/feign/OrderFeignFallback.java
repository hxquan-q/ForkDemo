package com.hxq.common.feign;

import com.hxq.common.entity.Order;
import com.hxq.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 订单服务Feign降级处理
 */
@Slf4j
@Component
public class OrderFeignFallback implements OrderFeignClient {

    @Override
    public Result<Order> getOrderById(Long id) {
        log.error("获取订单信息失败，订单ID: {}", id);
        return Result.failure("获取订单信息失败，服务降级");
    }

    @Override
    public Result<List<Order>> getOrdersByUserId(Long userId) {
        log.error("获取用户订单列表失败，用户ID: {}", userId);
        return Result.failure("获取用户订单列表失败，服务降级");
    }

    @Override
    public Result<Void> cancelOrder(Long id) {
        log.error("取消订单失败，订单ID: {}", id);
        return Result.failure("取消订单失败，服务降级");
    }

    @Override
    public Result<Boolean> checkOrderExist(Long userId, Long productId) {
        log.error("检查订单是否存在失败，用户ID: {}, 商品ID: {}", userId, productId);
        return Result.failure("检查订单是否存在失败，服务降级");
    }

    @Override
    public Result<Void> handleTimeoutOrders() {
        log.error("处理超时订单失败，服务降级");
        return Result.failure("处理超时订单失败，服务降级");
    }
}