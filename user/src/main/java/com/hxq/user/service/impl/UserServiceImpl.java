package com.hxq.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hxq.common.constant.Constants;
import com.hxq.common.exception.BusinessException;
import com.hxq.common.response.ResultCode;
import com.hxq.user.dto.LoginDTO;
import com.hxq.user.dto.UserDTO;
import com.hxq.user.entity.User;
import com.hxq.user.mapper.UserMapper;
import com.hxq.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public UserDTO login(LoginDTO loginDTO) {
        // 参数校验
        if (loginDTO == null || !StringUtils.hasText(loginDTO.getUsername())
                || !StringUtils.hasText(loginDTO.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "用户名或密码不能为空");
        }

        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = userMapper.selectOne(wrapper);

        // 用户不存在
        if (user == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "用户名或密码错误");
        }

        // 密码错误
        if (!user.getPassword().equals(loginDTO.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "用户名或密码错误");
        }

        // 生成token
        String token = UUID.randomUUID().toString().replace("-", "");

        // 暂时禁用Redis缓存，直接返回结果
        // TODO: 修复Redis连接问题后恢复
        // String tokenKey = Constants.Redis.USER_TOKEN_PREFIX + token;
        // redisTemplate.opsForValue().set(tokenKey, user, Constants.Redis.TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
        log.info("用户登录成功，Token: {}", token);

        // 构建返回对象
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setToken(token);

        return userDTO;
    }

    @Override
    public com.hxq.common.entity.User getUserById(Long id) {
        // 参数校验
        if (id == null || id <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "用户ID不能为空");
        }

        // 查询用户
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        // 转换为公共User对象
        com.hxq.common.entity.User commonUser = new com.hxq.common.entity.User();
        BeanUtils.copyProperties(user, commonUser);

        return commonUser;
    }

    @Override
    public com.hxq.common.entity.User getUserByToken(String token) {
        // 参数校验
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "Token不能为空");
        }

        // 暂时禁用Redis缓存，返回模拟用户信息
        // TODO: 修复Redis连接问题后恢复
        log.info("获取用户信息，Token: {}", token);

        // 返回模拟用户信息用于测试
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPhone("13800000000");
        user.setEmail("admin@example.com");

        // 转换为公共User对象
        com.hxq.common.entity.User commonUser = new com.hxq.common.entity.User();
        BeanUtils.copyProperties(user, commonUser);

        return commonUser;
    }
}