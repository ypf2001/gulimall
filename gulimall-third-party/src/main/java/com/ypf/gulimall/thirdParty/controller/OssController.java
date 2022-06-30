package com.ypf.gulimall.thirdParty.controller;

import com.ypf.common.utils.R;
import com.ypf.gulimall.thirdParty.service.uploadCertificate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-04 14:05
 **/

@RestController
public class OssController {
    @Resource
    uploadCertificate uploadCertificate;
    @RequestMapping("/oss/policy")
    public R policy(){
        Map map = new HashMap();
        map.put("token",uploadCertificate.uploadPolicy());
        map.put("dir","Images");
        return R.ok().put("data",map);
    }
    @PostMapping("/oss/upload")
    public String upload(@RequestParam("token") String token, @RequestParam("key") String key, @RequestParam("file") MultipartFile file){
        System.out.println("hello");
        System.out.println(file.getClass().getTypeName());
        String fileName = file.getOriginalFilename();
        // 获取文件后缀
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        // 若须要防止生成的临时文件重复,能够在文件名后添加随机码
        File res = null;
        try {
             res = File.createTempFile(fileName, prefix);
            file.transferTo(res);
        } catch (Exception e) {
            e.printStackTrace();
        }

            if( uploadCertificate.uploadImg(res, key,token)){
                return "{'success':'成功'}";
            }
        return "{'success':'失败'}";
    }
}
