package com.hxq.common.feign;

import com.hxq.common.entity.User;
import com.hxq.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 用户服务Feign降级处理
 */
@Slf4j
@Component
public class UserFeignFallback implements UserFeignClient {

    @Override
    public Result<User> getUserById(Long id) {
        log.error("获取用户信息失败，用户ID: {}", id);
        return Result.failure("获取用户信息失败，服务降级");
    }

    @Override
    public Result<User> getUserByToken(String token) {
        log.error("根据Token获取用户信息失败，Token: {}", token);
        return Result.failure("根据Token获取用户信息失败，服务降级");
    }
}