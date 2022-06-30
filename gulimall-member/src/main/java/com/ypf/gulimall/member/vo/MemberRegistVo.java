package com.ypf.gulimall.member.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-22 22:34
 **/
@Data
public class MemberRegistVo {

    private String username;

    private String password;

    private String email;
}
