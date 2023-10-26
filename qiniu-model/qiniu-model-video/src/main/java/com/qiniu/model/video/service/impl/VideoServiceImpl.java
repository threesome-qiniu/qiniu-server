package com.qiniu.model.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.mapper.VideoMapper;
import com.qiniu.model.video.service.IVideoService;
import com.qiniu.model.video.util.QiniuOssService;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.file.PathUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;

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
