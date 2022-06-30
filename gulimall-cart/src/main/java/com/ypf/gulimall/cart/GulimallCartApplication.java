package com.ypf.gulimall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-24 21:43
 **/
@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
public class GulimallCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallCartApplication.class,args);
    }
}
