package com.hxq.order.constant;

/**
 * 结果码常量类
 */
public class ResultCodeConstants {
    /**
     * 成功
     */
    public static final int SUCCESS = 200;

    /**
     * 未找到
     */
    public static final int NOT_FOUND = 404;

    /**
     * 参数错误
     */
    public static final int PARAM_ERROR = 400;

    /**
     * 服务器错误
     */
    public static final int FAILURE = 500;

    /**
     * 业务错误
     */
    public static final int BUSINESS_ERROR = 501;

    /**
     * 重复下单
     */
    public static final int REPEAT_ORDER = 4003;

    /**
     * 库存不足
     */
    public static final int STOCK_NOT_ENOUGH = 4004;
}