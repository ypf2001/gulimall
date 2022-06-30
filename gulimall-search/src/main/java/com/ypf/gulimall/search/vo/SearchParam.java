package com.ypf.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-16 15:19
 **/
@Data
public class SearchParam {
    //全文关键字
    private  String keyword;
    private  Long   catalog3Id;
    private  String sort="_asc";

    //过滤条件
    private  Integer hasStock;
    private String skuPrice;
    private List<Long>  brandId;
    private List<String> attrs;
    private Integer pageNum=1;
    /**
     * 原生的所有查询条件
     */
    private String _queryString;

}
