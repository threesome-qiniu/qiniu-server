package com.qiniu.service.user.controller.v1;

import com.qiniu.common.constant.Constants;
import com.qiniu.common.constant.TokenConstants;
import com.qiniu.common.domain.R;
import com.qiniu.common.exception.user.UserPasswordNotMatchException;
import com.qiniu.common.utils.JwtUtil;
import com.qiniu.model.user.domain.dto.LoginUserDTO;
import com.qiniu.model.user.domain.dto.RegisterBody;
import com.qiniu.service.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * UserController
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/login")
    public R<?> login(@RequestBody LoginUserDTO loginUserDTO) {
        log.debug("登录用户：{}", loginUserDTO);
        String token = userService.login(loginUserDTO);
        Map<String,String> map  = new HashMap<>();
        map.put(Constants.TOKEN, token);
        return R.ok(map);
    }

    @PostMapping("/register")
    public R<Boolean> register(@RequestBody RegisterBody registerBody) {
        log.debug("注册用户：{}", registerBody);
//        User user = userService.queryById(1L);
        boolean b = userService.register(registerBody);
        return R.ok(b);
    }

}
