package com.hxq.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求DTO
 */
@Data
public class LoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}