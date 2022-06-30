package com.ypf.gulimall.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-21 20:59
 **/
@Configuration
public class MyThreadConfig {
    @Bean
   public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties pool){
     return   new ThreadPoolExecutor(pool.getCoreSize(),
                  pool.getMaxSize(),
                pool.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
