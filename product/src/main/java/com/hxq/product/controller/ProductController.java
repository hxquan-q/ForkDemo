package com.hxq.product.controller;

import com.hxq.product.dto.ProductDTO;
import com.hxq.product.response.Result;
import com.hxq.product.service.ProductService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/api/product")
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 获取商品列表
     *
     * @return 商品列表
     */
    @GetMapping("/list")
    @CircuitBreaker(name = "productList", fallbackMethod = "productListFallback")
    public Result<List<ProductDTO>> list() {
        log.info("获取商品列表");
        List<ProductDTO> products = productService.listProducts();
        return Result.success(products);
    }

    /**
     * 获取商品详情
     *
     * @param id 商品ID
     * @return 商品详情
     */
    @GetMapping("/{id}")
    @CircuitBreaker(name = "productDetail", fallbackMethod = "productDetailFallback")
    public Result<ProductDTO> detail(@PathVariable("id") Long id) {
        log.info("获取商品详情, id: {}", id);
        ProductDTO product = productService.getProductDetail(id);
        return Result.success(product);
    }

    /**
     * 扣减库存
     *
     * @param productId 商品ID
     * @param quantity  数量
     * @return 结果
     */
    @PostMapping("/stock/deduct")
    @CircuitBreaker(name = "deductStock", fallbackMethod = "deductStockFallback")
    public Result<Void> deductStock(@RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity) {
        log.info("扣减库存, productId: {}, quantity: {}", productId, quantity);
        boolean result = productService.deductStock(productId, quantity);
        if (result) {
            return Result.success();
        } else {
            return Result.failure("扣减库存失败，库存不足");
        }
    }

    /**
     * 恢复库存
     *
     * @param productId 商品ID
     * @param quantity  数量
     * @return 结果
     */
    @PostMapping("/stock/restore")
    @CircuitBreaker(name = "restoreStock", fallbackMethod = "restoreStockFallback")
    public Result<Void> restoreStock(@RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity) {
        log.info("恢复库存, productId: {}, quantity: {}", productId, quantity);
        boolean result = productService.restoreStock(productId, quantity);
        if (result) {
            return Result.success();
        } else {
            return Result.failure("恢复库存失败");
        }
    }

    /**
     * 初始化商品库存到Redis
     *
     * @return 结果
     */
    @PostMapping("/stock/init")
    @CircuitBreaker(name = "initProductStockToRedis", fallbackMethod = "initProductStockToRedisFallback")
    public Result<Void> initProductStockToRedis() {
        log.info("初始化商品库存到Redis");
        productService.initProductStockToRedis();
        return Result.success();
    }

    /**
     * 商品列表接口熔断处理
     */
    public Result<List<ProductDTO>> productListFallback(Throwable throwable) {
        log.error("获取商品列表接口熔断", throwable);
        return Result.failure("系统繁忙，请稍后再试");
    }

    /**
     * 商品详情接口熔断处理
     */
    public Result<ProductDTO> productDetailFallback(Long id, Throwable throwable) {
        log.error("获取商品详情接口熔断, id: {}", id, throwable);
        return Result.failure("系统繁忙，请稍后再试");
    }

    /**
     * 扣减库存接口熔断处理
     */
    public Result<Void> deductStockFallback(Long productId, Integer quantity, Throwable throwable) {
        log.error("扣减库存接口熔断, productId: {}, quantity: {}", productId, quantity, throwable);
        return Result.failure("系统繁忙，请稍后再试");
    }

    /**
     * 恢复库存接口熔断处理
     */
    public Result<Void> restoreStockFallback(Long productId, Integer quantity, Throwable throwable) {
        log.error("恢复库存接口熔断, productId: {}, quantity: {}", productId, quantity, throwable);
        return Result.failure("系统繁忙，请稍后再试");
    }

    /**
     * 初始化商品库存到Redis接口熔断处理
     */
    public Result<Void> initProductStockToRedisFallback(Throwable throwable) {
        log.error("初始化商品库存到Redis接口熔断", throwable);
        return Result.failure("系统繁忙，请稍后再试");
    }
}