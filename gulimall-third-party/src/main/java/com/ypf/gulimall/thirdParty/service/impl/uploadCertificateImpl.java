package com.ypf.gulimall.thirdParty.service.impl;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.StringUtils;
import com.qiniu.util.UrlSafeBase64;
import com.ypf.gulimall.thirdParty.service.uploadCertificate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.File;


/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-04 01:09
 **/
@Slf4j
@Service
public class uploadCertificateImpl implements uploadCertificate {

    //构造一个带指定 Region 对象的配置类
   public Configuration configuration = new Configuration(Region.region1());
   public UploadManager uploadManager = new UploadManager(configuration);
    //...其他参数参考类注释
    //...生成上传凭证，然后准备上传
    private final String accessKey = "E6UbXTYy5O7GylbafNktW_ioNOnqMmwV-ZaBdwfI";
    private final String secretKey = "4egO9as32KpINzz-iij6yWmMfzdH-FpH6Ps2LtB3";
    private String bucket = "gulimall-product-ypf";
    //如果是Windows情况下，格式是 D:\\qiniu\\test.png
    //设置上传后的文件名称
    String key = "周记.docx";
    //默认不指定key的情况下，以文件内容的hash值作为文件名
    @Override
    public String uploadPolicy() {

        Auth auth = Auth.create(accessKey, secretKey);
        StringMap putPolicy = new StringMap();
        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":$(fsize)}");
        long expireSeconds = 3600;
        String upToken = auth.uploadToken(bucket, null, expireSeconds, putPolicy);
        return upToken;
    }

    @Override
    public boolean uploadImg(File file, String key, String token) {
        try {
            uploadManager.put(file,key,token);
            return true;

        } catch (QiniuException e) {
            throw new RuntimeException(e);

        }


    }

}
