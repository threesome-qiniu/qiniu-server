package com.qiniu.model.user.domain.dto;

/**
 * RegisterBody
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
public class RegisterBody extends LoginUserDTO{

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户密码
     */
    private String confirmPassword;
}
