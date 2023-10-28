package com.qiniu.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.common.exception.CustomException;
import com.qiniu.common.utils.IdUtils;
import com.qiniu.common.utils.JwtUtil;
import com.qiniu.model.common.enums.HttpCodeEnum;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.user.domain.dto.LoginUserDTO;
import com.qiniu.model.user.domain.dto.RegisterBody;
import com.qiniu.service.user.mapper.UserMapper;
import com.qiniu.service.user.service.IUserService;
import org.omg.CORBA.SystemException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

import static com.qiniu.model.common.enums.HttpCodeEnum.*;

/**
 * 用户表(User)表服务实现类
 *
 * @author roydon
 * @since 2023-10-24 19:18:26
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private UserMapper userMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param userId 主键
     * @return 实例对象
     */
    @Override
    public User queryById(Long userId) {
        return this.getById(userId);
    }

    @Override
    public String login(LoginUserDTO loginUserDTO) {
        //1.检查参数
        if(StringUtils.isBlank(loginUserDTO.getUsername()) || StringUtils.isBlank(loginUserDTO.getPassword())){
            throw new CustomException(SYSTEM_ERROR);
        }

        //2.查询用户
        User wmUser = getOne(Wrappers.<User>lambdaQuery().eq(User::getUserName, loginUserDTO.getUsername()));
        if(wmUser == null){
            throw new CustomException(USER_NOT_EXISTS);
        }

        //3.比对密码
        String salt = wmUser.getSalt();
        String pswd = loginUserDTO.getPassword();
        pswd = DigestUtils.md5DigestAsHex((pswd + salt).getBytes());
        if(pswd.equals(wmUser.getPassword())){
            //4.返回数据  jwt
            wmUser.setSalt("");
            wmUser.setPassword("");
//            map.put("user",wmUser);
            return JwtUtil.getToken(wmUser.getUserId());

        }else {
            throw new CustomException(USER_NOT_EXISTS);
        }
    }

    @Override
    public boolean register(RegisterBody registerBody) {
        //对数据进行非空判断
        if (StringUtils.isEmpty(registerBody.getUsername())) {
            throw new CustomException(USERNAME_NOT_NULL);
        }
        if (StringUtils.isEmpty(registerBody.getPassword())) {
            throw new CustomException(PASSWORD_NOT_NULL);
        }
        //判断username是否存在
        if (userNameExist(registerBody.getUsername())) {
            throw new CustomException(USERNAME_EXIST);
        }
        // 生成随机盐加密密码
        String fastUUID = IdUtils.fastUUID();
        String enPasswd = DigestUtils.md5DigestAsHex((registerBody.getPassword() + fastUUID).getBytes());
        User user = new User();
        user.setUserName(registerBody.getUsername());
        user.setPassword(enPasswd);
        user.setSalt(fastUUID);
        user.setNickName(IdUtils.shortUUID());
        return this.save(user);
    }

    private boolean userNameExist(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, username);
        return count(queryWrapper) > 0;
    }

}
