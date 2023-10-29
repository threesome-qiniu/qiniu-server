package com.qiniu.model.video.domain.dto;

import lombok.Data;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/10/29 19:55
 */
@Data
public class PageDto {

    private Long userId;
    private Integer pageNum=1;
    private Integer pageSize=10;

}

