package com.ypf.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ypf.common.utils.PageUtils;
import com.ypf.gulimall.order.entity.OrderItemEntity;

import java.util.Map;

/**
 * 订单项信息
 *
 * @author ypf
 * @email 2364555434@qq.com
 * @date 2022-05-28 23:56:06
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

