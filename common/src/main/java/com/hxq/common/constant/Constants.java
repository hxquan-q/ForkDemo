package com.hxq.common.constant;

/**
 * 通用常量类
 */
public interface Constants {

    /**
     * Redis相关常量
     */
    interface Redis {
        /**
         * 商品库存前缀
         */
        String PRODUCT_STOCK_PREFIX = "seckill:stock:";

        /**
         * 商品信息前缀
         */
        String PRODUCT_INFO_PREFIX = "seckill:product:";

        /**
         * 秒杀用户集合前缀
         */
        String SECKILL_USER_SET_PREFIX = "seckill:user:";

        /**
         * 秒杀商品库存分布式锁前缀
         */
        String SECKILL_STOCK_LOCK_PREFIX = "seckill:stock:lock:";

        /**
         * 订单锁前缀
         */
        String ORDER_LOCK_PREFIX = "order:lock:";

        /**
         * 用户令牌前缀
         */
        String USER_TOKEN_PREFIX = "user:token:";

        /**
         * 令牌过期时间（秒）
         */
        int TOKEN_EXPIRE_TIME = 7200;
    }

    /**
     * 秒杀相关常量
     */
    interface Seckill {
        /**
         * 秒杀商品已售罄标记
         */
        String STOCK_EMPTY_FLAG = "0";

        /**
         * 秒杀商品状态：未开始
         */
        Integer STATUS_NOT_START = 0;

        /**
         * 秒杀商品状态：进行中
         */
        Integer STATUS_IN_PROGRESS = 1;

        /**
         * 秒杀商品状态：已结束
         */
        Integer STATUS_END = 2;
    }

    /**
     * 订单相关常量
     */
    interface Order {
        /**
         * 订单状态：待支付
         */
        Integer STATUS_PENDING = 0;

        /**
         * 订单状态：已支付
         */
        Integer STATUS_PAID = 1;

        /**
         * 订单状态：已取消
         */
        Integer STATUS_CANCELED = 2;

        /**
         * 订单状态：已超时
         */
        Integer STATUS_TIMEOUT = 3;

        /**
         * 订单支付超时时间（分钟）
         */
        int PAY_TIMEOUT_MINUTES = 30;
    }

    /**
     * API相关常量
     */
    interface Api {
        /**
         * API路径前缀
         */
        String API_PREFIX = "/api";

        /**
         * 用户模块路径前缀
         */
        String USER_PREFIX = API_PREFIX + "/user";

        /**
         * 商品模块路径前缀
         */
        String PRODUCT_PREFIX = API_PREFIX + "/product";

        /**
         * 秒杀模块路径前缀
         */
        String SECKILL_PREFIX = API_PREFIX + "/seckill";

        /**
         * 订单模块路径前缀
         */
        String ORDER_PREFIX = API_PREFIX + "/order";
    }
}