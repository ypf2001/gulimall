package com.ypf.gulimall.product.service.impl;

import com.ypf.gulimall.product.vo.SkuItemVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypf.common.utils.PageUtils;
import com.ypf.common.utils.Query;

import com.ypf.gulimall.product.dao.SkuSaleAttrValueDao;
import com.ypf.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.ypf.gulimall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemVo.SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId) {
      List<SkuItemVo.SkuItemSaleAttrVo> saleAttrVos =   this.baseMapper.getSaleAttrsById(spuId);
        return saleAttrVos;
    }

    @Override
    public List<String> getSkuSaleAttrValuesList(Long skuId) {
        return this.baseMapper.getSkuSaleAttrValuesList(skuId);
    }

}