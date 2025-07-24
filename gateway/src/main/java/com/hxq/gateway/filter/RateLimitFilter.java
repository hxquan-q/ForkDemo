package com.hxq.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求限流过滤器
 */
@Component
@Slf4j
public class RateLimitFilter implements GlobalFilter, Ordered {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String RATE_LIMIT_PREFIX = "rate_limit:";

    // 秒杀接口特殊限制
    private static final String SECKILL_PATH = "/api/seckill/start";
    private static final int SECKILL_LIMIT_COUNT = 20; // 每秒钟最多处理20个请求

    // 普通接口限制
    private static final int DEFAULT_LIMIT_COUNT = 50; // 每秒钟最多处理50个请求

    @Autowired
    public RateLimitFilter(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 暂时禁用限流，让所有请求都通过
        log.debug("RateLimitFilter: 暂时禁用限流，让所有请求都通过");
        return chain.filter(exchange);

        /*
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 判断是否为需要限流的接口
        if (path.startsWith(SECKILL_PATH)) {
            return doRateLimit(exchange, chain, path, SECKILL_LIMIT_COUNT);
        } else {
            return doRateLimit(exchange, chain, path, DEFAULT_LIMIT_COUNT);
        }
        */
    }

    /**
     * 执行限流
     */
    private Mono<Void> doRateLimit(ServerWebExchange exchange, GatewayFilterChain chain,
            String path, int limitCount) {
        // 限流Key
        String key = RATE_LIMIT_PREFIX + path;

        // 使用Lua脚本进行限流，保证原子性
        String luaScript = "local key = KEYS[1]\n" +
                "local limitCount = tonumber(ARGV[1])\n" +
                "local currentCount = tonumber(redis.call('get', key) or '0')\n" +
                "if currentCount < limitCount then\n" +
                "    redis.call('incrby', key, 1)\n" +
                "    redis.call('expire', key, 1)\n" +
                "    return 1\n" +
                "else\n" +
                "    return 0\n" +
                "end";

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(Long.class);

        Long result = redisTemplate.execute(redisScript, Arrays.asList(key), limitCount);

        if (result != null && result == 1) {
            // 未超过限流，继续执行请求
            return chain.filter(exchange);
        } else {
            // 超过限流，返回错误信息
            return responseTooManyRequests(exchange);
        }
    }

    /**
     * 返回请求过多的响应
     */
    private Mono<Void> responseTooManyRequests(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", 429);
        result.put("message", "请求过于频繁，请稍后再试");

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(result);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("返回响应时发生错误", e);
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1; // 比认证过滤器先执行
    }
}