package com.hxq.product.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 秒杀请求DTO
 */
@Data
public class SeckillDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 购买数量
     */
    private Integer quantity;
}