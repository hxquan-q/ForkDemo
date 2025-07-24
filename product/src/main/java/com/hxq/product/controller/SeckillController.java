package com.hxq.product.controller;

import com.hxq.product.dto.SeckillDTO;
import com.hxq.product.response.Result;
import com.hxq.product.service.ProductService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 秒杀控制器
 */
@RestController
@RequestMapping("/api/seckill")
@Slf4j
public class SeckillController {

    @Resource
    private ProductService productService;

    /**
     * 执行秒杀
     *
     * @param productId 商品ID
     * @param userId    用户ID
     * @return 秒杀结果
     */
    @PostMapping("/start/{productId}")
    @CircuitBreaker(name = "seckill", fallbackMethod = "seckillFallback")
    public Result<String> doSeckill(
            @PathVariable("productId") Long productId,
            @RequestHeader("userId") Long userId) {
        log.info("执行秒杀, productId: {}, userId: {}", productId, userId);

        // 默认购买数量为1
        SeckillDTO seckillDTO = new SeckillDTO();
        seckillDTO.setUserId(userId);
        seckillDTO.setProductId(productId);
        seckillDTO.setQuantity(1);

        return productService.doSeckill(seckillDTO);
    }

    /**
     * 秒杀接口熔断处理
     */
    public Result<String> seckillFallback(Long productId, Long userId, Throwable throwable) {
        log.error("秒杀接口熔断, productId: {}, userId: {}", productId, userId, throwable);
        return Result.failure("系统繁忙，请稍后再试");
    }
}