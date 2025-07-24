package com.hxq.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hxq.product.constant.RedisConstants;
import com.hxq.product.constant.ResultCodeConstants;
import com.hxq.product.dto.ProductDTO;
import com.hxq.product.dto.SeckillDTO;
import com.hxq.product.dto.SeckillMessage;
import com.hxq.product.entity.Product;
import com.hxq.product.exception.BusinessException;
import com.hxq.product.mapper.ProductMapper;
import com.hxq.product.response.Result;
import com.hxq.product.service.ProductService;
import com.hxq.product.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 商品服务实现类
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired // 改用Autowired注解替代Resource
    private ProductMapper productMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 初始化商品库存到Redis
     * 系统启动时执行
     */
    @PostConstruct
    public void initProductStockToRedisOnStartup() {
        try {
            log.info("初始化商品库存到Redis");
            if (productMapper == null) {
                log.error("productMapper未注入，无法初始化商品库存");
                return;
            }

            if (redisTemplate == null) {
                log.error("redisTemplate未注入，无法初始化商品库存");
                return;
            }

            List<Product> products = productMapper.selectList(null);
            if (products == null || products.isEmpty()) {
                log.warn("没有找到商品数据");
                return;
            }

            for (Product product : products) {
                // 保存商品库存
                String stockKey = RedisConstants.PRODUCT_STOCK_PREFIX + product.getId();
                redisTemplate.opsForValue().set(stockKey, product.getStock());

                // 保存商品信息
                String productKey = RedisConstants.PRODUCT_INFO_PREFIX + product.getId();
                redisTemplate.opsForValue().set(productKey, product);

                log.info("商品{}初始化到Redis完成，库存：{}", product.getId(), product.getStock());
            }
        } catch (Exception e) {
            log.error("初始化商品库存到Redis失败", e);
            // 不抛出异常，避免影响应用启动
        }
    }

    /**
     * 初始化商品库存到Redis
     */
    @Override
    public void initProductStockToRedis() {
        initProductStockToRedisOnStartup();
    }

    /**
     * 获取商品列表
     */
    @Override
    public List<ProductDTO> listProducts() {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Product::getCreateTime);
        List<Product> products = productMapper.selectList(wrapper);

        List<ProductDTO> productDTOs = new ArrayList<>(products.size());
        for (Product product : products) {
            ProductDTO productDTO = convert(product);
            productDTOs.add(productDTO);
        }

        return productDTOs;
    }

    /**
     * 获取商品详情
     */
    @Override
    public ProductDTO getProductDetail(Long id) {
        // 先从Redis中获取
        String productKey = RedisConstants.PRODUCT_INFO_PREFIX + id;
        Object productObj = redisTemplate.opsForValue().get(productKey);
        Product product;

        if (productObj != null) {
            product = (Product) productObj;
        } else {
            // Redis中不存在，从数据库中获取
            product = productMapper.selectById(id);
            if (product == null) {
                throw new BusinessException(ResultCodeConstants.NOT_FOUND, "商品不存在");
            }

            // 保存到Redis
            redisTemplate.opsForValue().set(productKey, product);
        }

        return convert(product);
    }

    /**
     * 根据ID获取商品
     */
    @Override
    public Product getProductById(Long id) {
        // 先从Redis中获取
        String productKey = RedisConstants.PRODUCT_INFO_PREFIX + id;
        Object productObj = redisTemplate.opsForValue().get(productKey);

        if (productObj != null) {
            return (Product) productObj;
        } else {
            // Redis中不存在，从数据库中获取
            Product product = productMapper.selectById(id);
            if (product == null) {
                throw new BusinessException(ResultCodeConstants.NOT_FOUND, "商品不存在");
            }

            // 保存到Redis
            redisTemplate.opsForValue().set(productKey, product);
            return product;
        }
    }

    /**
     * 扣减库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStock(Long productId, Integer quantity) {
        // 更新数据库库存
        int result = productMapper.deductStock(productId, quantity);
        if (result > 0) {
            // 更新Redis库存
            String stockKey = RedisConstants.PRODUCT_STOCK_PREFIX + productId;
            Long remainStock = redisTemplate.opsForValue().decrement(stockKey, quantity);
            log.info("商品{}扣减库存成功，数量：{}，剩余库存：{}", productId, quantity, remainStock);
            return true;
        }
        return false;
    }

    /**
     * 恢复库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean restoreStock(Long productId, Integer quantity) {
        // 更新数据库库存
        int result = productMapper.restoreStock(productId, quantity);
        if (result > 0) {
            // 更新Redis库存
            String stockKey = RedisConstants.PRODUCT_STOCK_PREFIX + productId;
            Long remainStock = redisTemplate.opsForValue().increment(stockKey, quantity);
            log.info("商品{}恢复库存成功，数量：{}，剩余库存：{}", productId, quantity, remainStock);
            return true;
        }
        return false;
    }

    /**
     * 执行秒杀
     */
    @Override
    public Result<String> doSeckill(SeckillDTO seckillDTO) {
        Long userId = seckillDTO.getUserId();
        Long productId = seckillDTO.getProductId();
        Integer quantity = seckillDTO.getQuantity();

        // 参数校验
        if (userId == null || productId == null || quantity == null || quantity <= 0) {
            return Result.failure(ResultCodeConstants.PARAM_ERROR, "参数错误");
        }

        // 获取商品信息
        Product product;
        try {
            product = getProductById(productId);
        } catch (BusinessException e) {
            return Result.failure(e.getCode(), e.getMessage());
        }

        // 检查秒杀时间
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(product.getStartTime())) {
            return Result.failure(ResultCodeConstants.SECKILL_NOT_START, "秒杀未开始");
        }
        if (now.isAfter(product.getEndTime())) {
            return Result.failure(ResultCodeConstants.SECKILL_END, "秒杀已结束");
        }

        // 检查是否重复购买
        String userKey = RedisConstants.SECKILL_USER_SET_PREFIX + productId;
        Boolean isMember = redisTemplate.opsForSet().isMember(userKey, userId);
        if (Boolean.TRUE.equals(isMember)) {
            return Result.failure(ResultCodeConstants.REPEAT_ORDER, "您已经参与过此秒杀");
        }

        // 检查Redis库存
        String stockKey = RedisConstants.PRODUCT_STOCK_PREFIX + productId;
        Object stockObj = redisTemplate.opsForValue().get(stockKey);
        if (stockObj == null) {
            return Result.failure(ResultCodeConstants.SECKILL_NO_STOCK, "商品已售罄");
        }

        int stock = Integer.parseInt(stockObj.toString());
        if (stock < quantity) {
            return Result.failure(ResultCodeConstants.SECKILL_NO_STOCK, "商品库存不足");
        }

        // 使用分布式锁控制并发
        String lockKey = RedisConstants.SECKILL_STOCK_LOCK_PREFIX + productId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试获取锁，最多等待3秒，锁过期时间10秒
            boolean isLocked = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                return Result.failure(ResultCodeConstants.FAILURE, "系统繁忙，请稍后再试");
            }

            // 再次检查库存
            stockObj = redisTemplate.opsForValue().get(stockKey);
            if (stockObj == null) {
                return Result.failure(ResultCodeConstants.SECKILL_NO_STOCK, "商品已售罄");
            }

            stock = Integer.parseInt(stockObj.toString());
            if (stock < quantity) {
                return Result.failure(ResultCodeConstants.SECKILL_NO_STOCK, "商品库存不足");
            }

            // 预扣Redis库存
            redisTemplate.opsForValue().decrement(stockKey, quantity);

            // 记录用户参与秒杀
            redisTemplate.opsForSet().add(userKey, userId);
            redisTemplate.expire(userKey, 24, TimeUnit.HOURS); // 设置过期时间

            // 发送消息到MQ
            SeckillMessage message = new SeckillMessage();
            message.setUserId(userId);
            message.setProductId(productId);
            message.setCreateTime(LocalDateTime.now());

            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE,
                    RabbitMQConfig.ORDER_ROUTING_KEY, message);

            log.info("秒杀成功，发送消息到MQ：userId={}, productId={}, quantity={}", userId, productId, quantity);

            return Result.success("秒杀成功");
        } catch (InterruptedException e) {
            log.error("获取分布式锁异常", e);
            return Result.failure("系统繁忙，请稍后再试");
        } finally {
            // 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 转换Product为ProductDTO
     */
    private ProductDTO convert(Product product) {
        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(product, productDTO);

        // 计算秒杀状态
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(product.getStartTime())) {
            // 秒杀未开始
            productDTO.setStatusDesc("秒杀未开始");
            productDTO.setRemainSeconds(Duration.between(now, product.getStartTime()).getSeconds());
        } else if (now.isAfter(product.getEndTime())) {
            // 秒杀已结束
            productDTO.setStatusDesc("秒杀已结束");
            productDTO.setRemainSeconds(-1L);
        } else {
            // 秒杀进行中
            productDTO.setStatusDesc("秒杀进行中");
            productDTO.setRemainSeconds(Duration.between(now, product.getEndTime()).getSeconds());
        }

        return productDTO;
    }
}