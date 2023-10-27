package com.qiniu.model.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiniu.model.video.domain.Video;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频表(Video)表服务接口
 *
 * @author roydon
 * @since 2023-10-25 20:33:11
 */
public interface IVideoService extends IService<Video> {

    /**
     * 上传视频文件，返回文件url
     * @param file
     * @return
     */
    String uploadVideo(MultipartFile file);

}
