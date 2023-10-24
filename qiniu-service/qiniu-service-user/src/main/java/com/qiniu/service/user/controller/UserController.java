package com.qiniu.service.user.controller;

import com.qiniu.model.user.domain.User;
import com.qiniu.model.user.domain.dto.LoginUserDTO;
import com.qiniu.model.user.domain.dto.RegisterBody;
import com.qiniu.model.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserController
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/login")
    public String login(@RequestBody LoginUserDTO loginUserDTO) {
        log.debug("登录用户：{}", loginUserDTO);
        return loginUserDTO.toString();
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterBody registerBody) {
        log.debug("注册用户：{}", registerBody);
        User user = userService.queryById(1L);
        return registerBody.toString();
    }

}
