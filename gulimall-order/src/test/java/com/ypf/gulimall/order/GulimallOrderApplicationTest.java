package com.ypf.gulimall.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-27 15:01
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallOrderApplicationTest {
    @Autowired
    AmqpAdmin amqpAdmin;

    @Test
    public void test1() {
        //public DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments) {
        amqpAdmin.declareExchange(new DirectExchange("hello-java-exchange", true, false) {
        });
        log.info("创建成功");
    }

    @Test
    public void createQ() {
        amqpAdmin.declareQueue(new Queue("hello-java-queue", true, false, false));
        log.info("创建成功");
    }
}
