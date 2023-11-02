package com.qiniu.service.video.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qiniu.model.video.domain.VideoUserFavorites;
import com.qiniu.model.video.dto.VideoPageDto;
import com.qiniu.model.video.vo.VideoUserVo;

import java.util.List;

/**
 * 视频收藏表(VideoUserFavorites)表服务接口
 *
 * @author lzq
 * @since 2023-10-31 15:57:38
 */
public interface IVideoUserFavoritesService extends IService<VideoUserFavorites> {

    boolean videoFavorites(String videoId);

    List<VideoUserVo> userFavorites(Long userId);

    IPage queryFavoritePage(VideoPageDto pageDto);
}
