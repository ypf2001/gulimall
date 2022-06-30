package com.ypf.gulimall.member;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class GulimallMemberApplicationTests {

    @Test
    public void contextLoads() {
        System.out.println(DigestUtils.md5Hex("123456 "));
    }

}
