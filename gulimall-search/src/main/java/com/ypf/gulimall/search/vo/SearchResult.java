package com.ypf.gulimall.search.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ypf.common.to.es.SkuEsModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-16 15:41
 **/

@Data
public class SearchResult {
    /**
     * 查询到的所有商品
     */
    private List<SkuEsModel> products;
    private Integer pageNum = 1;
    private Long total;
    private Integer totalPages = 1;
    private List<BrandVo> brands;
    /*
     * 当前查询到的结果，所有涉及到的所有属性
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private List<AttrVo> attrs;

    /**
     * 当前查询到的结果，所有涉及到的所有分类
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private List<CatalogVo> catalogs;
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NavVo {
        private String navName;
        private String navValue;
        private String link;
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CatalogVo {

        private Long catelogId;

        private String catelogName;
    }
}
