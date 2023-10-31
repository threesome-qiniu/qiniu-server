package com.qiniu.service.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiniu.model.video.domain.VideoUserLike;
import com.qiniu.model.video.vo.VideoUserVo;

import java.util.List;

/**
 * 点赞表(VideoUserLike)表服务接口
 *
 * @author lzq
 * @since 2023-10-30 14:33:00
 */
public interface IVideoUserLikeService extends IService<VideoUserLike> {

    boolean videoLike(String videoId);

    List<VideoUserVo> userLikes(Long userId);
}
