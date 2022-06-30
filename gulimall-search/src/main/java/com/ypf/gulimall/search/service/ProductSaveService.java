package com.ypf.gulimall.search.service;

import com.ypf.common.to.es.SkuEsModel;

import java.util.List;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-11 00:07
 **/

public interface ProductSaveService {

    Boolean productStatusUp(List<SkuEsModel> skuEsModels);
}
