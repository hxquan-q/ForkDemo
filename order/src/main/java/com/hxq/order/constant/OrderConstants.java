package com.hxq.order.constant;

/**
 * 订单相关常量
 */
public class OrderConstants {
    /**
     * 订单状态：待支付
     */
    public static final int STATUS_PENDING = 0;

    /**
     * 订单状态：已支付
     */
    public static final int STATUS_PAID = 1;

    /**
     * 订单状态：已取消
     */
    public static final int STATUS_CANCELED = 2;

    /**
     * 订单状态：已超时
     */
    public static final int STATUS_TIMEOUT = 3;

    /**
     * 订单支付超时时间（分钟）
     */
    public static final int ORDER_TIMEOUT_MINUTES = 30;

    /**
     * API前缀
     */
    public static final String API_ORDER_PREFIX = "/api/order";

    /**
     * Redis订单前缀
     */
    public static final String REDIS_ORDER_KEY_PREFIX = "order:";
}