package com.ypf.gulimall.order.vo;

import lombok.Data;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-28 21:21
 **/
@Data
public class SkuStockVo {
    private  Long skuId;
    private  Boolean hasStock;
}
