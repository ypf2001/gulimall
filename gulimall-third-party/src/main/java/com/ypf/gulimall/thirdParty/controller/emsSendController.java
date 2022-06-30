package com.ypf.gulimall.thirdParty.controller;

import com.ypf.common.utils.R;
import com.ypf.gulimall.thirdParty.component.emsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-22 16:14
 **/
@RestController
@RequestMapping("ems")
public class emsSendController {
@Autowired
    emsComponent emsComponent;
    @GetMapping("/sendCode")
    R sendCode(@RequestParam("email") String  email, @RequestParam("code") String code) throws MessagingException {
        emsComponent.sendMailMsg(email,code);
        return R.ok();
    };

}
