package com.qiniu.model.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qiniu.model.user.domain.User;

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

}