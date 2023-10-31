package com.qiniu.model.video.vo;

import lombok.Data;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/10/31 9:23
 */
@Data
public class VideoUserVo {
    // 视频链接
    private String videoUrl;
    // 视频播放量
    private Long viewNum;
    // 视频点赞量
    private Long LikeNum;
    //视频收藏量
    private Long favoritesNum;

}
