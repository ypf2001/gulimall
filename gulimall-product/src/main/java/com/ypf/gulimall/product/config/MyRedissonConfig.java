package com.ypf.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-14 21:21
 **/
@Configuration
public class MyRedissonConfig {
    /**
     * @Param: 
     * @return:    
     * @Date:  2022/6/14/23:32 
     */
    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer()
                // .setScanInterval(2000) // 集群状态扫描间隔时间，单位是毫秒
                //可以用"rediss://"来启用SSL连接
                .setAddress("redis://1.117.229.165:6379");

        return Redisson.create(config);

    }

}
