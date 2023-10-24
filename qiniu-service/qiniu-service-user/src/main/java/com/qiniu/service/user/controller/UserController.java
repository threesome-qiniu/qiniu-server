package com.qiniu.service.user.controller;

import com.qiniu.model.user.domain.dto.LoginUserDTO;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/login")
    public String login(@RequestBody LoginUserDTO loginUserDTO) {
        log.debug("登录用户：{}",loginUserDTO);
        return loginUserDTO.toString();
    }

}
