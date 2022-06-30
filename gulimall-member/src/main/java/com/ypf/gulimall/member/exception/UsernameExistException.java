package com.ypf.gulimall.member.exception;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-22 23:02
 **/
public class UsernameExistException extends RuntimeException{
    public UsernameExistException() {
        super("用户名已经存在异常");
    }
}
