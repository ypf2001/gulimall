package com.ypf.gulimall.thirdParty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-03 16:32
 **/
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallThirdPartyApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallThirdPartyApplication.class,args);
    }
}
