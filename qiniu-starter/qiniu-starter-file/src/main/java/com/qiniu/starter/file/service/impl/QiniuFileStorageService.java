package com.qiniu.starter.file.service.impl;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.starter.file.config.QiniuOssConfig;
import com.qiniu.starter.file.config.QiniuOssConfigProperties;
import com.qiniu.starter.file.service.FileStorageService;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.web.multipart.MultipartFile;
import sun.dc.pr.PRError;

import java.io.InputStream;

@Slf4j
@EnableConfigurationProperties(QiniuOssConfigProperties.class)
@Import(QiniuOssConfig.class)
public class QiniuFileStorageService implements FileStorageService {

    @Autowired
    private UploadManager uploadManager;

    @Autowired
    private QiniuOssConfigProperties qiniuOssConfigProperties;

    private final static String separator = "/";

    /**
     * 上传图片文件
     */
    @Override
    public String uploadImgFile(MultipartFile file, String prefix, String filePath) {
        try {
            InputStream inputStream = file.getInputStream();
            String upToken = Auth.create(qiniuOssConfigProperties.getAccessKey(), qiniuOssConfigProperties.getSecretKey())
                    .uploadToken(qiniuOssConfigProperties.getBucket());
            try {
                Response response = uploadManager.put(inputStream, filePath, upToken, null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                return prefix + filePath;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception ex) {
            //ignore
        }
        return "www";
    }
}