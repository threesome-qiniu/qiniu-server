package com.qiniu.model.video.domain.dto;

import lombok.Data;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/10/29 14:58
 */
@Data
public class VideoBindDto {

    /**
     * 视频id
     */
    private String videoId;

    /**
     * 视频标题
     */
    private String videoTitle;

    /**
     * 视频描述
     */
    private String videoDesc;

    /**
     * 视频链接
     */
    private String videoUrl;

}
