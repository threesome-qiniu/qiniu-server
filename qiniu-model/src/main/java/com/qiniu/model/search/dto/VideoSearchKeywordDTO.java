package com.qiniu.model.search.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * VideoSearchWordDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 * 搜索dto
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoSearchKeywordDTO extends PageDTO{

    private String keyword;

}
