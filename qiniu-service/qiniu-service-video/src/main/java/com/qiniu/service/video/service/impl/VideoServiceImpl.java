package com.qiniu.service.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.common.utils.file.PathUtils;
import com.qiniu.model.video.domain.Video;
import com.qiniu.service.video.mapper.VideoMapper;
import com.qiniu.service.video.service.IVideoService;
import com.qiniu.service.video.util.QiniuOssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 视频表(Video)表服务实现类
 *
 * @author roydon
 * @since 2023-10-25 20:33:11
 */
@Slf4j
@Service("videoService")
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements IVideoService {

    @Resource
    private VideoMapper videoMapper;

    @Autowired
    private QiniuOssService qiniuOssService;

    @Override
    public String uploadVideo(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        //对原始文件名进行判断
//        if(!originalFilename.endsWith(".png")){
//            throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
//        }
        //上传文件到OSS
        assert originalFilename != null;
        String filePath = PathUtils.generateFilePath(originalFilename);
        String url = qiniuOssService.uploadOss(file, filePath);
        log.info("视频上传地址：{}", url);
        return url;
    }

}
