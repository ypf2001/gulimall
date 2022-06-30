package com.ypf.gulimall.thirdParty.service;

import java.io.File;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-04 13:33
 **/
public interface uploadCertificate {
    //默认不指定key的情况下，以文件内容的hash值作为文件名
    String uploadPolicy();
    boolean uploadImg(File file, String key, String token );
}
