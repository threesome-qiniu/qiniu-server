package com.qiniu.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.common.context.UserContext;
import com.qiniu.common.exception.CustomException;
import com.qiniu.common.utils.string.StringUtils;
import com.qiniu.model.common.dto.PageDTO;
import com.qiniu.model.common.enums.HttpCodeEnum;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.user.domain.UserFollow;
import com.qiniu.service.user.mapper.UserFollowMapper;
import com.qiniu.service.user.service.IUserFollowService;
import com.qiniu.service.user.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户关注表(UserFollow)表服务实现类
 *
 * @author roydon
 * @since 2023-10-30 15:54:21
 */
@Service("userFollowService")
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow> implements IUserFollowService {
    @Resource
    private UserFollowMapper userFollowMapper;

    @Resource
    private IUserService userService;

    @Override
    public boolean followUser(Long userId) {
        Long loginUserId = UserContext.getUser().getUserId();
        if (StringUtils.isNull(userId) || StringUtils.isNull(loginUserId)) {
            return false;
        }
        if (loginUserId.equals(userId)) {
            // 不可关注自己
            throw new CustomException(HttpCodeEnum.NOT_ALLOW_FOLLOW_YOURSELF);
        }
        User user = userService.queryById(userId);
        if (StringUtils.isNull(user)) {
            // 用户不存在
            throw new CustomException(HttpCodeEnum.USER_NOT_EXIST);
        }
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getUserId, loginUserId);
        queryWrapper.eq(UserFollow::getUserFollowId, userId);
        List<UserFollow> list = list(queryWrapper);
        if (!list.isEmpty()) {
            // 已关注
            throw new CustomException(HttpCodeEnum.ALREADY_FOLLOW);
        }
        return this.save(new UserFollow(loginUserId, userId));
    }

    /**
     * 取消关注
     *
     * @param userId 取消关注用户id
     */
    @Override
    public boolean unFollowUser(Long userId) {
        Long loginUserId = UserContext.getUser().getUserId();
        if (StringUtils.isNull(userId) || StringUtils.isNull(loginUserId)) {
            return false;
        }
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getUserId, loginUserId);
        queryWrapper.eq(UserFollow::getUserFollowId, userId);
        this.remove(queryWrapper);
        return true;
    }

    /**
     * 分页查询用户关注列表
     *
     * @param pageDTO 分页对象
     * @return IPage<User>
     */
    @Override
    public IPage<User> followPage(PageDTO pageDTO) {
        LambdaQueryWrapper<UserFollow> userFollowLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userFollowLambdaQueryWrapper.eq(UserFollow::getUserId, UserContext.getUserId());
        List<Long> userFollowUserIdList = this.list(userFollowLambdaQueryWrapper).stream().map(UserFollow::getUserFollowId).collect(Collectors.toList());
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.in(User::getUserId, userFollowUserIdList);
        return userService.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()),userLambdaQueryWrapper);
    }
}
