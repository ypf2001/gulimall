package com.ypf.gulimall.thirdParty.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-22 15:37
 **/
@Component
@Slf4j
public class emsComponent {
    @Autowired
    JavaMailSenderImpl mailSender;
    public void sendMailMsg(@RequestParam("email") String email,@RequestParam("code") String code ) throws MessagingException {
        int count=1;//默认发送一次
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
        log.info(code);
            //标题
            helper.setSubject("您的验证码为："+code);
            //内容
            helper.setText("您好！，感谢支持谷粒商城。您的验证码为："+"<h2>"+code+"</h2>"+"请注意保管！",true);
            //邮件接收者
            helper.setTo(email);
            //邮件发送者，必须和配置文件里的一样，不然授权码匹配不上
            helper.setFrom("2364555434@qq.com");
            mailSender.send(mimeMessage);
            log.info("邮件发送成功！"+(count+1));
    }
}
