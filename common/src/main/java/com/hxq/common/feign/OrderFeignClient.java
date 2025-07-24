package com.hxq.common.feign;

import com.hxq.common.entity.Order;
import com.hxq.common.response.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 订单服务Feign客户端
 */
@FeignClient(name = "order-service", fallback = OrderFeignFallback.class)
public interface OrderFeignClient {

    /**
     * 根据订单ID查询订单
     */
    @GetMapping("/api/order/{id}")
    Result<Order> getOrderById(@PathVariable("id") Long id);

    /**
     * 根据用户ID查询订单列表
     */
    @GetMapping("/api/order/list")
    Result<List<Order>> getOrdersByUserId(@RequestParam("userId") Long userId);

    /**
     * 取消订单
     */
    @PostMapping("/api/order/cancel/{id}")
    Result<Void> cancelOrder(@PathVariable("id") Long id);

    /**
     * 根据商品ID查询用户是否已经购买过该商品
     */
    @GetMapping("/api/order/check")
    Result<Boolean> checkOrderExist(@RequestParam("userId") Long userId, @RequestParam("productId") Long productId);

    /**
     * 处理超时未支付的订单
     */
    @PostMapping("/api/order/timeout/handle")
    Result<Void> handleTimeoutOrders();
}