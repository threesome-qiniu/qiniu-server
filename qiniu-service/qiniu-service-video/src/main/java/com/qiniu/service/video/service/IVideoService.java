package com.qiniu.service.video.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.dto.VideoFeedDTO;
import com.qiniu.model.video.dto.VideoPageDto;
import com.qiniu.model.video.dto.VideoBindDto;
import com.qiniu.model.video.vo.VideoUserLikeAndFavoriteVo;
import com.qiniu.model.video.vo.VideoVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 视频表(Video)表服务接口
 *
 * @author roydon
 * @since 2023-10-25 20:33:11
 */
public interface IVideoService extends IService<Video> {

    /**
     * 上传视频文件，返回文件url
     *
     * @param file
     * @return
     */
    String uploadVideo(MultipartFile file);

    Video selectById(String id);

    /**
     * 上传
     *
     * @param videoBindDto
     * @return
     */
    String videoPublish(VideoBindDto videoBindDto);

    /**
     * 分页我的视频
     *
     * @param pageDto
     * @return
     */
    IPage<Video> queryMyVideoPage(VideoPageDto pageDto);

    /**
     * 分页用户视频
     *
     * @param pageDto
     * @return
     */
    IPage<Video> queryUserVideoPage(VideoPageDto pageDto);

    /**
     * 视频feed接口
     *
     * @param videoFeedDTO createTime
     * @return video
     */
    VideoVO feedVideo(VideoFeedDTO videoFeedDTO);

    /**
     * 用户点赞列表分页接口
     *
     * @param pageDto
     * @return
     */

    List<VideoUserLikeAndFavoriteVo> queryMyLikeVideoPage(VideoPageDto pageDto);

    /**
     * 用户收藏列表分页接口
     *
     * @param pageDto
     * @return
     */
    List<VideoUserLikeAndFavoriteVo> queryMyFavoritesVideoPage(VideoPageDto pageDto);
}
