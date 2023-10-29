package com.qiniu.service.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.microsoft.schemas.office.visio.x2012.main.VisioDocumentDocument1;
import com.qiniu.common.exception.CustomException;
import com.qiniu.common.utils.file.PathUtils;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.domain.dto.VideoBindDto;
import com.qiniu.service.video.mapper.VideoMapper;
import com.qiniu.service.video.service.IVideoService;
import com.qiniu.service.video.util.QiniuOssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerWebExchange;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

import static com.qiniu.model.common.enums.HttpCodeEnum.*;

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
    public String uploadVideo(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String url="";
        //对文件id进行判断，如果文件已经存在，则不上传，直接返回数据库中文件的存储路径
        String id="";
        try {
            InputStream inputStream = file.getInputStream();
            id=DigestUtils.md5DigestAsHex(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Video videodb = selectById(id);
        if (videodb!= null){
            url=videodb.getVideoUrl();
        }else {
            assert originalFilename != null;
            String filePath = PathUtils.generateFilePath(originalFilename,file);
            url = qiniuOssService.uploadOss(file, filePath);
            log.info("视频上传地址：{}", url);
        }

        return url;
    }

    @Override
    public Video selectById(String id) {
        Video video = videoMapper.selectById(id);
        return video;
    }

    @Override
    public Video bindVideoAndUser(VideoBindDto videoBindDto, User user) {

        if (videoBindDto.getVideoTitle().length()<=0&&videoBindDto.getVideoTitle().length()>30){
            throw new CustomException(BIND_CONTENT_TITLE_FAIL);
        }
        if (videoBindDto.getVideoDesc().length()<=0&&videoBindDto.getVideoDesc().length()>200){
            throw new CustomException(BIND_CONTENT_DESC_FAIL);
        }
        Video video = new Video();
        video.setVideoId(videoBindDto.getVideoId());
        video.setVideoUrl(videoBindDto.getVideoUrl());
        video.setVideoTitle(videoBindDto.getVideoTitle());
        video.setVideoDesc(videoBindDto.getVideoDesc());
        video.setUserId(user.getUserId());
        video.setCreateTime(LocalDateTime.now());
        video.setCreateBy(user.getUserName());
        int insert = videoMapper.insert(video);
        if (insert == 0){
            throw new CustomException(BIND_FAIL);
        }
        return videoMapper.selectById(videoBindDto.getVideoId());
    }


}
