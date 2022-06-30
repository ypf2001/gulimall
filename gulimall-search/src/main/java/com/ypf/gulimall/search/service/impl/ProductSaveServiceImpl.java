package com.ypf.gulimall.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.ypf.common.to.es.SkuEsModel;
import com.ypf.gulimall.search.constant.EsConstant;
import com.ypf.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-11 00:10
 **/
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    ElasticsearchClient elasticsearchClient;

    @Override
    public Boolean productStatusUp(List<SkuEsModel> skuEsModels) {

        //建立映射关系
        //保存数据
        //构造保存请求
        List<BulkResponse> responses = new ArrayList<>();
        skuEsModels.forEach(skuEsModel -> {
            try {
                responses.add(elasticsearchClient.bulk(BulkRequest.of(fn -> {
                    fn.operations(op -> {
                        op.create(c -> {
                            c.index(EsConstant.PRODUCT_INDEX);
                            c.id(skuEsModel.getSkuId().toString());
                            c.document(skuEsModel);
                            return c;
                        });
                        return op;
                    });
                    return fn;
                })));
            } catch (IOException e) {
                log.error("商品上架失败");
            }
        });
        AtomicInteger errCount = new AtomicInteger();
        List<String> collect = responses.stream().map(res -> {
            if (res.errors()) {
                errCount.getAndIncrement();
                return res.items().stream().map(item -> item.id()).toString();
            }
            return null;
        }).collect(Collectors.toList());
        if (errCount.get() > 0) {
            log.error("商品上架错误{}", collect.size());
            return false;
        }
        return true;
    }
}
