package com.qiniu.model.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.user.domain.dto.RegisterBody;
import com.qiniu.model.user.mapper.UserMapper;
import com.qiniu.model.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
    public User register(RegisterBody registerBody) {

        return null;
    }

}
