package com.ypf.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ypf.common.utils.PageUtils;
import com.ypf.gulimall.order.entity.OrderEntity;
import com.ypf.gulimall.order.vo.OrderConfirmVo;
import com.ypf.gulimall.order.vo.OrderSubmitVo;
import com.ypf.gulimall.order.vo.SubmitOrderResponseVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author ypf
 * @email 2364555434@qq.com
 * @date 2022-05-28 23:56:06
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);


    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo);
}

