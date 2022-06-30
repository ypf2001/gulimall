package com.ypf.gulimall.member.exception;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-22 23:01
 **/
public class EmailExistException extends RuntimeException{
    public EmailExistException() {
        super("邮箱已经存在异常");
    }
}
