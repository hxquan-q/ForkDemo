package com.hxq.common.feign;

import com.hxq.common.entity.User;
import com.hxq.common.response.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "user-service", fallback = UserFeignFallback.class)
public interface UserFeignClient {

    /**
     * 根据ID查询用户信息
     */
    @GetMapping("/api/user/info")
    Result<User> getUserById(@RequestParam("id") Long id);

    /**
     * 根据Token获取用户信息
     */
    @GetMapping("/api/user/info/token")
    Result<User> getUserByToken(@RequestParam("token") String token);
}