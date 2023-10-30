package com.qiniu.service.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.common.context.UserContext;
import com.qiniu.common.exception.CustomException;
import com.qiniu.common.utils.bean.BeanCopyUtils;
import com.qiniu.common.utils.file.PathUtils;
import com.qiniu.common.utils.uniqueid.IdGenerator;
import com.qiniu.model.video.domain.Video;
import com.qiniu.model.video.domain.dto.VideoPageDto;
import com.qiniu.model.video.domain.dto.VideoBindDto;
import com.qiniu.service.video.mapper.VideoMapper;
import com.qiniu.service.video.service.IVideoService;
import com.qiniu.service.video.util.QiniuOssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

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
        String url = "";
        //对文件id进行判断，如果文件已经存在，则不上传，直接返回数据库中文件的存储路径
        String id = "";
        try {
            InputStream inputStream = file.getInputStream();
            id = DigestUtils.md5DigestAsHex(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Video videodb = selectById(id);
        if (videodb != null) {
            url = videodb.getVideoUrl();
        } else {
            assert originalFilename != null;
            String filePath = PathUtils.generateFilePath(originalFilename, file);
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
    public Video bindVideoAndUser(VideoBindDto videoBindDto) {
        Long userId = UserContext.getUser().getUserId();
        //判断传过来的数据是否符合数据库字段标准
        if (videoBindDto.getVideoTitle().length() > 30) {
            throw new CustomException(BIND_CONTENT_TITLE_FAIL);
        }
        if (videoBindDto.getVideoDesc().length() > 200) {
            throw new CustomException(BIND_CONTENT_DESC_FAIL);
        }
        Video video = BeanCopyUtils.copyBean(videoBindDto, Video.class);
        video.setVideoId(IdGenerator.generatorShortId());
        video.setUserId(userId);
        video.setCreateTime(LocalDateTime.now());
        video.setCreateBy(userId.toString());
        this.save(video);
        return video;
    }

    @Override
    public IPage<Video> queryMyVideoPage(VideoPageDto pageDto) {
        Long userId = UserContext.getUser().getUserId();
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Video::getUserId, userId);
        queryWrapper.like(StringUtils.isNotEmpty(pageDto.getVideoTitle()), Video::getVideoTitle, pageDto.getVideoTitle());
        return this.page(new Page<>(pageDto.getPageNum(), pageDto.getPageSize()), queryWrapper);
    }

    @Override
    public IPage<Video> queryUserVideoPage(VideoPageDto pageDto) {
        if (com.qiniu.common.utils.string.StringUtils.isNull(pageDto.getUserId())) {
            return new Page<>();
        }
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Video::getUserId, pageDto.getUserId());
        queryWrapper.like(StringUtils.isNotEmpty(pageDto.getVideoTitle()), Video::getVideoTitle, pageDto.getVideoTitle());
        return this.page(new Page<>(pageDto.getPageNum(), pageDto.getPageSize()), queryWrapper);
    }

}
