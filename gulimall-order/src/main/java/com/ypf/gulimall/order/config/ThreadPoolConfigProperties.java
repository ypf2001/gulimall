package com.ypf.gulimall.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-21 21:04
 **/
@ConfigurationProperties(prefix="gulimall.thread")
@Component
@Data
@Primary
public class ThreadPoolConfigProperties {
    private Integer coreSize;
    private  Integer maxSize;
    private Integer keepAliveTime;
}
