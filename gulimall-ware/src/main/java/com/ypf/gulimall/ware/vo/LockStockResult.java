package com.ypf.gulimall.ware.vo;

import lombok.Data;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-29 16:41
 **/
@Data
public class LockStockResult {
    private Long skuId;
    private Integer num;
    private  boolean locked;

}
