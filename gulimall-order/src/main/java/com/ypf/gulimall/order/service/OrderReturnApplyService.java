package com.ypf.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ypf.common.utils.PageUtils;
import com.ypf.gulimall.order.entity.OrderReturnApplyEntity;

import java.util.Map;

/**
 * 订单退货申请
 *
 * @author ypf
 * @email 2364555434@qq.com
 * @date 2022-05-28 23:56:05
 */
public interface OrderReturnApplyService extends IService<OrderReturnApplyEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

