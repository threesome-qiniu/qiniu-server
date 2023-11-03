package com.qiniu.service.social.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qiniu.common.domain.R;
import com.qiniu.common.domain.vo.PageDataInfo;
import com.qiniu.feign.user.RemoteUserService;
import com.qiniu.model.common.dto.PageDTO;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.social.UserFollow;
import com.qiniu.service.social.service.IUserFollowService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户关注表(UserFollow)表控制层
 *
 * @author roydon
 * @since 2023-10-30 15:54:19
 */
@RestController
@RequestMapping("/api/v1/follow")
public class UserFollowController {

    @Resource
    private IUserFollowService userFollowService;

    @Resource
    private RemoteUserService remoteUserService;

    /**
     * 关注
     */
    @GetMapping("/{userId}")
    public R<?> follow(@PathVariable("userId") Long userId) {
        return R.ok(userFollowService.followUser(userId));
    }

    /**
     * 取消关注
     */
    @DeleteMapping("/{userId}")
    public R<?> unfollow(@PathVariable("userId") Long userId) {
        return R.ok(userFollowService.unFollowUser(userId));
    }

    /**
     * 分页查询我的关注列表
     */
    @PostMapping("/page")
    public PageDataInfo followPage(@RequestBody PageDTO pageDTO) {
        IPage<UserFollow> userFollowIPage = userFollowService.followPage(pageDTO);
        List<User> userList = new ArrayList<>();
        userFollowIPage.getRecords().forEach(uf->{
            User user = remoteUserService.userInfoById(uf.getUserFollowId()).getData();
            userList.add(user);
        });
        return PageDataInfo.genPageData(userList, userFollowIPage.getTotal());
    }

}

