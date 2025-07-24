package com.hxq.product.constant;

/**
 * RabbitMQ常量类
 */
public class RabbitConstants {

    /**
     * 订单队列名称
     */
    public static final String ORDER_QUEUE = "order.queue";

    /**
     * 订单交换机名称
     */
    public static final String ORDER_EXCHANGE = "order.exchange";

    /**
     * 订单路由键
     */
    public static final String ORDER_ROUTING_KEY = "order.routing.key";

    /**
     * 订单延迟队列名称（用于处理超时未支付订单）
     */
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";

    /**
     * 订单延迟交换机名称
     */
    public static final String ORDER_DELAY_EXCHANGE = "order.delay.exchange";

    /**
     * 订单延迟路由键
     */
    public static final String ORDER_DELAY_ROUTING_KEY = "order.delay.routing.key";
}