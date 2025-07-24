package com.hxq.common.feign;

import com.hxq.common.entity.Product;
import com.hxq.common.response.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 商品服务Feign客户端
 */
@FeignClient(name = "product-service", fallback = ProductFeignFallback.class)
public interface ProductFeignClient {

    /**
     * 根据ID查询商品
     */
    @GetMapping("/api/product/{id}")
    Result<Product> getProductById(@PathVariable("id") Long id);

    /**
     * 扣减库存
     */
    @PostMapping("/api/product/stock/deduct")
    Result<Void> deductStock(@RequestParam("productId") Long productId, @RequestParam("quantity") Integer quantity);

    /**
     * 恢复库存
     */
    @PostMapping("/api/product/stock/restore")
    Result<Void> restoreStock(@RequestParam("productId") Long productId, @RequestParam("quantity") Integer quantity);

    /**
     * 初始化商品库存到Redis
     */
    @PostMapping("/api/product/stock/init")
    Result<Void> initProductStockToRedis();
}