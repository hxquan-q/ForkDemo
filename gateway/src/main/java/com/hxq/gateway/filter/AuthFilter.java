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
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局鉴权过滤器
 */
@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    public AuthFilter(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 无需鉴权的路径
     */
    private static final String[] WHITE_LIST = {
            "/api/user/login",
            "/api/user/register",
            "/api/product/list",
            "/api/product/*"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 暂时禁用鉴权，让所有请求都通过
        log.debug("AuthFilter: 暂时禁用鉴权，让所有请求都通过");
        return chain.filter(exchange);

        /*
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 判断是否为白名单路径
        if (isWhiteList(path)) {
            return chain.filter(exchange);
        }

        // 获取请求头中的token
        String token = request.getHeaders().getFirst("Authorization");

        // 如果token为空，返回未授权
        if (!StringUtils.hasText(token)) {
            return unauthorized(exchange);
        }

        // 检查token是否在Redis中存在
        String key = "user:token:" + token;
        Object userObj = redisTemplate.opsForValue().get(key);

        // 如果token无效，返回未授权
        if (userObj == null) {
            return unauthorized(exchange);
        }

        // 继续执行请求
        return chain.filter(exchange);
        */
    }

    /**
     * 判断是否为白名单路径
     */
    private boolean isWhiteList(String path) {
        for (String pattern : WHITE_LIST) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", 401);
        result.put("message", "未登录或登录已过期");

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
        return 0;
    }
}