package com.ypf.gulimall.cart.to;

import lombok.Data;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-24 23:51
 **/
@Data
public class UserInfoTo {
    private Long userId;
    private String userKey;
    private boolean tempUser = false;
}
