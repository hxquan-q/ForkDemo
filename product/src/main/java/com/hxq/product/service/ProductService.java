package com.hxq.product.service;

import com.hxq.product.dto.ProductDTO;
import com.hxq.product.dto.SeckillDTO;
import com.hxq.product.entity.Product;
import com.hxq.product.response.Result;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService {

    /**
     * 获取商品列表
     *
     * @return 商品列表
     */
    List<ProductDTO> listProducts();

    /**
     * 获取商品详情
     *
     * @param id 商品ID
     * @return 商品信息
     */
    ProductDTO getProductDetail(Long id);

    /**
     * 根据ID获取商品
     *
     * @param id 商品ID
     * @return 商品信息
     */
    Product getProductById(Long id);

    /**
     * 扣减库存
     *
     * @param productId 商品ID
     * @param quantity  数量
     * @return 是否成功
     */
    boolean deductStock(Long productId, Integer quantity);

    /**
     * 恢复库存
     *
     * @param productId 商品ID
     * @param quantity  数量
     * @return 是否成功
     */
    boolean restoreStock(Long productId, Integer quantity);

    /**
     * 执行秒杀
     *
     * @param seckillDTO 秒杀信息
     * @return 秒杀结果
     */
    Result<String> doSeckill(SeckillDTO seckillDTO);

    /**
     * 初始化商品库存到Redis
     */
    void initProductStockToRedis();
}