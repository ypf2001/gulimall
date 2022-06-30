package com.ypf.gulimall.product.vo;

import com.ypf.gulimall.product.entity.SkuImagesEntity;
import com.ypf.gulimall.product.entity.SkuInfoEntity;
import com.ypf.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-20 15:04
 **/
@Data
public class SkuItemVo {
    SkuInfoEntity info;
    List<SkuImagesEntity> images;
    SpuInfoDescEntity desp;
    boolean hasStock = true;
    List<SkuItemSaleAttrVo> saleAttr;
    List<SpuItemAttrGroupVo> attrGroup;
    @Data
    public static class SkuItemSaleAttrVo {
        private Long attrId;
        private String attrName;
        private List<AttrValueWithSkuIdVo> attrValues;
    }

    @Data
    public static class SpuItemAttrGroupVo {
        private String groupName;
        private List<SpuBaseAttrVo> attrs;
    }

    @Data
    public static class SpuBaseAttrVo {
        private String attrName;
        private String attrValue;
    }
}
