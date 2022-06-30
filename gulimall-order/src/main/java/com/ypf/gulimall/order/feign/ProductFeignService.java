package com.ypf.gulimall.order.feign;

import com.ypf.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-29 12:06
 **/
@FeignClient("gulimall-product")
public interface ProductFeignService {
    @GetMapping("/product/spuinfo/{skuId}/get")
     R getSpuInfoBySkuId(@PathVariable("skuId") Long skuId);

}
