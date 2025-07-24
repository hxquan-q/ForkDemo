package com.hxq.user.controller;

import com.hxq.common.constant.Constants;
import com.hxq.common.entity.User;
import com.hxq.common.response.Result;
import com.hxq.user.dto.LoginDTO;
import com.hxq.user.dto.UserDTO;
import com.hxq.user.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户控制器
 */
@RestController
@RequestMapping(Constants.Api.USER_PREFIX)
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 用户信息
     */
    @PostMapping("/login")
    public Result<UserDTO> login(@RequestBody LoginDTO loginDTO) {
        log.info("用户登录: {}", loginDTO.getUsername());
        UserDTO userDTO = userService.login(loginDTO);
        return Result.success(userDTO);
    }

    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/info")
    @CircuitBreaker(name = "getUserInfo", fallbackMethod = "getUserInfoFallback")
    public Result<User> getUserInfo(@RequestParam("id") Long id) {
        log.info("获取用户信息, 用户ID: {}", id);
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    /**
     * 根据Token获取用户信息
     *
     * @param token 令牌
     * @return 用户信息
     */
    @GetMapping("/info/token")
    @CircuitBreaker(name = "getUserInfoByToken", fallbackMethod = "getUserInfoByTokenFallback")
    public Result<User> getUserInfoByToken(@RequestParam("token") String token) {
        log.info("根据Token获取用户信息, Token: {}", token);
        User user = userService.getUserByToken(token);
        return Result.success(user);
    }

    /**
     * 获取用户信息熔断降级处理
     */
    public Result<User> getUserInfoFallback(Long id, Throwable throwable) {
        log.error("获取用户信息接口熔断, 用户ID: {}", id, throwable);
        return Result.failure("系统繁忙，请稍后再试");
    }

    /**
     * 根据Token获取用户信息熔断降级处理
     */
    public Result<User> getUserInfoByTokenFallback(String token, Throwable throwable) {
        log.error("根据Token获取用户信息接口熔断, Token: {}", token, throwable);
        return Result.failure("系统繁忙，请稍后再试");
    }
}