package com.ypf.gulimall.cart.feign;

import com.ypf.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-25 14:37
 **/
@FeignClient(name ="gulimall-product" )
public interface ProductFeignService {
    @RequestMapping("/product/skuinfo/info/{skuId}")
    //@RequiresPermissions("product:skuinfo:info")
     R info(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skusaleattrvalue/stringList/{skuId}")
     List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);
    @GetMapping({"/product/skuinfo/{skuId}/price"})
    R getPrice(@PathVariable("skuId") Long skuId );

}
