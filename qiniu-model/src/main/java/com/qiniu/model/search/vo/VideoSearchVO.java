package com.qiniu.model.search.vo;

import java.util.Date;

/**
 * VideoSearchVO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
public class VideoSearchVO {

    // 文章id
    private Long id;
    // 文章标题
    private String title;
    // 文章发布时间
    private Date publishTime;
    // 文章布局
    private Integer layout;
    // 封面
    private String images;
    // 作者id
    private Long authorId;
    // 作者名词
    private String authorName;
    //静态url
    private String staticUrl;
    //文章内容
    private String content;
}
