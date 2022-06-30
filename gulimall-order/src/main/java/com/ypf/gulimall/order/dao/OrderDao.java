package com.ypf.gulimall.order.dao;

import com.ypf.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author ypf
 * @email 2364555434@qq.com
 * @date 2022-05-28 23:56:06
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
