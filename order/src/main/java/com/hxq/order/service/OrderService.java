package com.hxq.order.service;

import com.hxq.order.dto.OrderDTO;
import com.hxq.order.dto.SeckillMessage;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建订单
     *
     * @param message 秒杀消息
     * @return 订单ID
     */
    Long createOrder(SeckillMessage message);

    /**
     * 获取订单详情
     *
     * @param id 订单ID
     * @return 订单DTO
     */
    OrderDTO getOrderById(Long id);

    /**
     * 获取用户订单列表
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    List<OrderDTO> getOrdersByUserId(Long userId);

    /**
     * 取消订单
     *
     * @param id 订单ID
     * @return 是否成功
     */
    boolean cancelOrder(Long id);

    /**
     * 检查用户是否已购买商品
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 是否存在
     */
    boolean checkOrderExist(Long userId, Long productId);

    /**
     * 处理超时未支付的订单
     */
    void handleTimeoutOrders();
}