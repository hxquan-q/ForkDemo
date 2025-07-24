package com.hxq.common.response;

/**
 * 响应码常量
 */
public interface ResultCode {
    /**
     * 成功
     */
    Integer SUCCESS = 200;

    /**
     * 失败
     */
    Integer FAILURE = 500;

    /**
     * 未认证
     */
    Integer UNAUTHORIZED = 401;

    /**
     * 未授权
     */
    Integer FORBIDDEN = 403;

    /**
     * 资源不存在
     */
    Integer NOT_FOUND = 404;

    /**
     * 参数错误
     */
    Integer PARAM_ERROR = 400;

    /**
     * 业务错误
     */
    Integer BUSINESS_ERROR = 501;

    /**
     * 限流
     */
    Integer FLOW_LIMITING = 429;

    /**
     * 秒杀商品已抢完
     */
    Integer SECKILL_NO_STOCK = 1001;

    /**
     * 秒杀未开始
     */
    Integer SECKILL_NOT_START = 1002;

    /**
     * 秒杀已结束
     */
    Integer SECKILL_END = 1003;

    /**
     * 重复下单
     */
    Integer REPEAT_ORDER = 1004;
}