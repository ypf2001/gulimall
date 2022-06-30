package com.ypf.gulimall.order.to;

import com.ypf.gulimall.order.entity.OrderEntity;
import com.ypf.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-29 11:21
 **/
@Data
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItemEntity> orderItems;
    private BigDecimal payPrice;
    private BigDecimal fare;
}
