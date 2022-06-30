package com.ypf.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-29 16:37
 **/
@Data
public class WareSkuLockVo {
    private String orderSn;
    private List<OrderItemVo> locks;
}
