package com.ypf.gulimall.order.feign;

import com.ypf.common.utils.R;
import com.ypf.gulimall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-28 21:16
 **/
@FeignClient("gulimall-ware")
public interface WmsFeignService {
    @PostMapping("/ware/waresku/hasStock")
     R getSkuHasStock(@RequestBody List<Long> skuIds);

    @PostMapping("/ware/waresku/lock/order")
     R orderLock(@RequestBody WareSkuLockVo vo);
}
