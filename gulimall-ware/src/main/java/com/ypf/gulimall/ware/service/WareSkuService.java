package com.ypf.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ypf.common.utils.PageUtils;
import com.ypf.gulimall.ware.entity.WareSkuEntity;
import com.ypf.gulimall.ware.vo.LockStockResult;
import com.ypf.gulimall.ware.vo.SkuHasStockVo;
import com.ypf.gulimall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:59:40
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);


    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);


    Boolean orderLockStock(WareSkuLockVo vo);
}

