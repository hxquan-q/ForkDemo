package com.hxq.product.constant;

/**
 * Redis常量类
 */
public class RedisConstants {

    /**
     * 商品库存前缀
     */
    public static final String PRODUCT_STOCK_PREFIX = "seckill:stock:";

    /**
     * 商品信息前缀
     */
    public static final String PRODUCT_INFO_PREFIX = "seckill:product:";

    /**
     * 秒杀用户集合前缀
     */
    public static final String SECKILL_USER_SET_PREFIX = "seckill:user:";

    /**
     * 秒杀商品库存分布式锁前缀
     */
    public static final String SECKILL_STOCK_LOCK_PREFIX = "seckill:stock:lock:";
}