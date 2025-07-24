package com.hxq.user.service;

import com.hxq.common.entity.User;
import com.hxq.user.dto.LoginDTO;
import com.hxq.user.dto.UserDTO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 用户信息
     */
    UserDTO login(LoginDTO loginDTO);

    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Long id);

    /**
     * 根据Token获取用户信息
     *
     * @param token 用户令牌
     * @return 用户信息
     */
    User getUserByToken(String token);
}