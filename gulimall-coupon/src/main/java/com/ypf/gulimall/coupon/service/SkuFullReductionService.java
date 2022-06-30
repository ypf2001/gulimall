package com.ypf.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ypf.common.to.SkuReductionTo;
import com.ypf.common.utils.PageUtils;
import com.ypf.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author ypf
 * @email 2364555434@qq.com
 * @date 2022-05-28 23:21:50
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo skuReductionTo);
}

