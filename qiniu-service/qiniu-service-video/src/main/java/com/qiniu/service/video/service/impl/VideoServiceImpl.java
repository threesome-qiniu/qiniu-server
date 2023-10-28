package com.qiniu.service.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.common.utils.IdUtils;
import com.qiniu.common.utils.file.PathUtils;
import com.qiniu.model.video.domain.Video;
import com.qiniu.service.video.mapper.VideoMapper;
import com.qiniu.service.video.service.IVideoService;
import com.qiniu.service.video.util.QiniuOssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

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

    @Transactional
    @Override
    public String uploadVideo(MultipartFile file) throws UnsupportedEncodingException {
        String originalFilename = file.getOriginalFilename();

        //对原始文件名进行判断
        //如果文件名重复，则对文件名重新命名
        //构造一个带指定 Region 对象的配置类
//        if(!originalFilename.endsWith(".png")){
//          throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
//        }
        //上传文件到OSS
        assert originalFilename != null;
        String filePath = PathUtils.generateFilePath(originalFilename);
        String url = qiniuOssService.uploadOss(file, filePath);
        Video video = new Video();
        video.setVideoId(IdUtils.fastSimpleUUID());
        video.setUserId(1L);
        video.setVideoUrl(url);
        video.setCreateBy("lzq");
        video.setCreateTime(LocalDateTime.now());
        video.setVideoDesc("视频简介");
        video.setVideoTitle("治愈");
        videoMapper.insert(video);
        log.info("视频上传地址：{}", url);
        return url;
    }

}
