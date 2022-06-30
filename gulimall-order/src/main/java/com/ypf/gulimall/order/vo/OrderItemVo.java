package com.ypf.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-28 16:36
 **/
@Data
public class OrderItemVo {
    private Long skuId;
    private String title;
    private String image;

    /**
     * 商品套餐属性
     */
    private List<String> skuAttrValues;

    private BigDecimal price;

    private Integer count;

    private BigDecimal totalPrice;
}
