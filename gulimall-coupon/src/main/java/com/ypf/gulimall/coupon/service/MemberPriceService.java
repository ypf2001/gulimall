package com.ypf.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ypf.common.utils.PageUtils;
import com.ypf.gulimall.coupon.entity.MemberPriceEntity;

import java.util.Map;

/**
 * 商品会员价格
 *
 * @author ypf
 * @email 2364555434@qq.com
 * @date 2022-05-28 23:21:49
 */
public interface MemberPriceService extends IService<MemberPriceEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

