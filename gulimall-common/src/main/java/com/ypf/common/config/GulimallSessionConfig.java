package com.ypf.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-23 22:18
 **/
@Configuration
public class GulimallSessionConfig {
@Bean
    public CookieSerializer cookieSerializer(){
    DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
    defaultCookieSerializer.setCookieName("GULISESSION");
    return defaultCookieSerializer;
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
return  new GenericJackson2JsonRedisSerializer(new ObjectMapper());
    }
}
