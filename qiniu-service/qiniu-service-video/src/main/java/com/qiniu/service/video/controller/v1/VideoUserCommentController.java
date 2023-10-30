package com.qiniu.service.video.controller.v1;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qiniu.common.context.UserContext;
import com.qiniu.common.domain.R;
import com.qiniu.common.domain.vo.PageDataInfo;
import com.qiniu.common.utils.bean.BeanCopyUtils;
import com.qiniu.common.utils.string.StringUtils;
import com.qiniu.common.utils.uniqueid.IdGenerator;
import com.qiniu.feign.user.RemoteUserService;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.video.domain.VideoUserComment;
import com.qiniu.model.video.dto.VideoUserCommentPageDTO;
import com.qiniu.model.video.vo.VideoUserCommentVO;
import com.qiniu.service.video.service.IVideoUserCommentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * (VideoUserComment)表控制层
 *
 * @author roydon
 * @since 2023-10-30 16:52:51
 */
@RestController
@RequestMapping("/api/v1/comment")
public class VideoUserCommentController {

    @Resource
    private IVideoUserCommentService videoUserCommentService;

    @Resource
    private RemoteUserService remoteUserService;

    /**
     * 分页查询评论集合树
     */
    @PostMapping("/tree")
    public PageDataInfo queryTree(@RequestBody VideoUserCommentPageDTO pageDTO) {
        String newsId = pageDTO.getVideoId();
        if (StringUtil.isEmpty(newsId)) {
            R.ok();
        }
//        videoUserCommentService.generatorVideoCommentPageTree(VideoUserCommentPageDTO pageDTO);
        IPage<VideoUserComment> iPage = this.videoUserCommentService.getRootListByVideoId(pageDTO);
        List<VideoUserComment> rootRecords = iPage.getRecords();
        List<VideoUserCommentVO> voList = new ArrayList<>();
        rootRecords.forEach(r -> {
            // 获取用户详情
            VideoUserCommentVO appNewsCommentVO = BeanCopyUtils.copyBean(r, VideoUserCommentVO.class);
            Long userId = r.getUserId();
            User user = remoteUserService.userInfoById(userId).getData();
            if (StringUtils.isNotNull(user)) {
                appNewsCommentVO.setNickName(user.getNickName());
                appNewsCommentVO.setAvatar(user.getAvatar());
            }
            Long commentId = r.getCommentId();
            List<VideoUserComment> children = this.videoUserCommentService.getChildren(commentId);
            List<VideoUserCommentVO> childrenVOS = BeanCopyUtils.copyBeanList(children, VideoUserCommentVO.class);
            childrenVOS.forEach(c -> {
                User cUser = remoteUserService.userInfoById(c.getUserId()).getData();
                if (StringUtils.isNotNull(cUser)) {
                    c.setNickName(cUser.getNickName());
                    c.setAvatar(cUser.getAvatar());
                }
                if (!c.getParentId().equals(commentId)) {
                    // 回复了回复
                    VideoUserComment byId = this.videoUserCommentService.getById(c.getParentId());
                    c.setReplayUserId(byId.getUserId());
                    User byUser = remoteUserService.userInfoById(c.getUserId()).getData();
                    if (StringUtils.isNotNull(byUser)) {
                        c.setReplayUserNickName(byUser.getNickName());
                    }
                }
            });
            appNewsCommentVO.setChildren(childrenVOS);
            voList.add(appNewsCommentVO);
        });
        return new PageDataInfo(R.SUCCESS, "查询成功", voList, iPage.getTotal());
    }

    /**
     * 新增评论
     */
    @PostMapping
    public R<?> add(@RequestBody VideoUserComment videoUserComment) {
        videoUserComment.setCreateTime(LocalDateTime.now());
        videoUserComment.setUserId(UserContext.getUser().getUserId());
        return R.ok(this.videoUserCommentService.save(videoUserComment));
    }

    /**
     * 回复评论
     */
    @PostMapping("/replay")
    public R<?> replay(@RequestBody VideoUserComment videoUserComment) {
        return R.ok(this.videoUserCommentService.replay(videoUserComment));
    }

    /**
     * 删除数据
     */
    @DeleteMapping("{commentId}")
    public R<?> removeById(@PathVariable Long commentId) {
        return R.ok(this.videoUserCommentService.delCommentByUser(commentId));
    }
}

