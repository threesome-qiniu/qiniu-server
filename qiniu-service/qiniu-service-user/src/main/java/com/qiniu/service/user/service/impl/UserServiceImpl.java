package com.qiniu.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.common.exception.CustomException;
import com.qiniu.common.service.RedisService;
import com.qiniu.common.utils.IdUtils;
import com.qiniu.common.utils.JwtUtil;
import com.qiniu.common.utils.string.StringUtils;
import com.qiniu.model.common.enums.HttpCodeEnum;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.user.domain.dto.LoginUserDTO;
import com.qiniu.model.user.domain.dto.RegisterBody;
import com.qiniu.model.user.domain.dto.UserThreadLocalUtil;
import com.qiniu.service.user.constants.UserCacheConstants;
import com.qiniu.service.user.mapper.UserMapper;
import com.qiniu.service.user.service.IUserService;
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

    @Resource
    private RedisService redisService;

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
        if (StringUtils.isBlank(loginUserDTO.getUsername()) || StringUtils.isBlank(loginUserDTO.getPassword())) {
            throw new CustomException(SYSTEM_ERROR);
        }
        //2.查询用户
        User dbUser = getOne(Wrappers.<User>lambdaQuery().eq(User::getUserName, loginUserDTO.getUsername()));
        if (dbUser == null) {
            throw new CustomException(USER_NOT_EXISTS);
        }
        //3.比对密码
        String salt = dbUser.getSalt();
        String pswd = loginUserDTO.getPassword();
        pswd = DigestUtils.md5DigestAsHex((pswd + salt).getBytes());
        if (pswd.equals(dbUser.getPassword())) {
            return JwtUtil.getToken(dbUser.getUserId());
        } else {
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
        if (!registerBody.getPassword().equals(registerBody.getConfirmPassword())) {
            throw new CustomException(CONFIRM_PASSWORD_NOT_MATCH);
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

    @Override
    public User updateUserInfo(User user) {
        Long userId = UserThreadLocalUtil.getUser().getUserId();
        if (StringUtils.isNull(userId)) {
            throw new CustomException(HttpCodeEnum.NEED_LOGIN);
        }
        // 先删除缓存
        redisService.deleteObject(UserCacheConstants.USER_INFO_PREFIX + userId);
        user.setUserId(userId);
        boolean update = this.updateById(user);
        if (update) {
            return user;
        } else {
            return new User();
        }
    }
}
