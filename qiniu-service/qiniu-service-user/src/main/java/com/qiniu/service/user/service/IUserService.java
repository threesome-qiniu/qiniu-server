package com.qiniu.service.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.user.domain.dto.LoginUserDTO;
import com.qiniu.model.user.domain.dto.RegisterBody;

/**
 * 用户表(User)表服务接口
 *
 * @author roydon
 * @since 2023-10-24 19:18:25
 */
public interface IUserService extends IService<User> {

    /**
     * 通过ID查询单条数据
     *
     * @param userId 主键
     * @return 实例对象
     */
    User queryById(Long userId);

    /**
     * 用户注册
     *
     * @param registerBody
     * @return
     */
    boolean register(RegisterBody registerBody);

    String login(LoginUserDTO loginUserDTO);
}
