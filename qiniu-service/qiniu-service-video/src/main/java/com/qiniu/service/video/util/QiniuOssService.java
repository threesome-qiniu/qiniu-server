package com.qiniu.service.video.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.service.video.config.QiniuOssConfig;
import com.qiniu.service.video.constants.QiniuVideoOssConstants;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * QiniuOssUtil
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/26
 **/
@Component
public class QiniuOssService {

    @Autowired
    QiniuOssConfig qiniuOssConfig;

    public String uploadOss(MultipartFile file, String filePath) {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        //cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V1;
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //默认不指定key的情况下，以文件内容的hash值作为文件名

        try {
            InputStream inputStream = file.getInputStream();
            Auth auth = Auth.create(qiniuOssConfig.getAccessKey(), qiniuOssConfig.getSecretKey());
            String upToken = auth.uploadToken(qiniuOssConfig.getBucket());
            try {
                Response response = uploadManager.put(inputStream, filePath, upToken, null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                return QiniuVideoOssConstants.PREFIX_URL + filePath;
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
