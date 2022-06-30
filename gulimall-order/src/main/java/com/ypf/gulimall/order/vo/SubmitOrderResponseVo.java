package com.ypf.gulimall.order.vo;

import com.ypf.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-29 00:55
 **/
@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;
    //0成功 1失败
    private Integer  code;

}
