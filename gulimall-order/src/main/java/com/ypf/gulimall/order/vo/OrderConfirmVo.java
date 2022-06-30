package com.ypf.gulimall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-28 10:28
 **/

public class OrderConfirmVo {
    //收货地址
    @Getter
    @Setter
    List<MemberAddressVo> address;
    @Getter
    @Setter
    List<OrderItemVo> items;
    @Getter
    @Setter
    Integer integeration;
    @Setter
    @Getter
    String orderToken;
    @Setter
    @Getter
    Map<Long, Boolean> stocks;

    public Integer getCount() {
        Integer sum = 0;
        if (items != null) {
            for (OrderItemVo item : items) {
                sum += item.getCount();
            }
        }
        return sum;
    }
//    @Getter
//    @Setter
//    BigDecimal payPrice;

    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if (items != null) {
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount()));
                sum = sum.add(multiply);
            }
        }
        return sum;
    }

    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
