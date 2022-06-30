package com.ypf.gulimall.order.vo;

import lombok.Data;
import org.springframework.core.annotation.Order;

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
