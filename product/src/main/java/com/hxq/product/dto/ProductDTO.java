package com.hxq.product.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品DTO
 */
@Data
public class ProductDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;

    /**
     * 商品库存
     */
    private Integer stock;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 秒杀开始时间
     */
    private LocalDateTime startTime;

    /**
     * 秒杀结束时间
     */
    private LocalDateTime endTime;

    /**
     * 秒杀状态：0-未开始，1-进行中，2-已结束
     */
    private Integer status;

    /**
     * 秒杀状态描述
     */
    private String statusDesc;

    /**
     * 剩余时间（单位：秒）
     */
    private Long remainSeconds;
}