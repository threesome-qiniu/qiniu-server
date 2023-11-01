package com.qiniu.service.user.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qiniu.common.domain.R;
import com.qiniu.common.domain.vo.PageDataInfo;
import com.qiniu.model.common.dto.PageDTO;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.user.domain.UserFollow;
import com.qiniu.service.user.service.IUserFollowService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户关注表(UserFollow)表控制层
 *
 * @author roydon
 * @since 2023-10-30 15:54:19
 */
@RestController
@RequestMapping("/api/v1")
public class UserFollowController {

    @Resource
    private IUserFollowService userFollowService;

    /**
     * 关注
     */
    @GetMapping("/follow/{userId}")
    public R<?> follow(@PathVariable("userId") Long userId) {
        return R.ok(userFollowService.followUser(userId));
    }

    /**
     * 取消关注
     */
    @GetMapping("/unfollow/{userId}")
    public R<?> unfollow(@PathVariable("userId") Long userId) {
        return R.ok(userFollowService.unFollowUser(userId));
    }

    /**
     * 分页查询我的关注列表
     */
    @PostMapping("/followpage")
    public PageDataInfo followPage(@RequestBody PageDTO pageDTO) {
        IPage<User> userIPage = userFollowService.followPage(pageDTO);
        return PageDataInfo.genPageData(userIPage.getRecords(), userIPage.getTotal());
    }

}

