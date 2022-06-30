package com.ypf.gulimall.product.feign;

import com.ypf.common.utils.R;
import com.ypf.gulimall.product.vo.SkuHasStockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-10 23:20
 **/
@FeignClient("gulimall-ware")
public interface WareFeignService {
    //查询sku是否有库存
    @PostMapping("/ware/waresku/hasStock")
     R<List< SkuHasStockVo>>  getSkuHasStock(@RequestBody List<Long> skuIds);
}
