package com.ypf.gulimall.order.feign;

import com.ypf.gulimall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-28 16:36
 **/
@FeignClient("gulimall-cart")
public interface CartFeignService {
    @GetMapping("/currentUserCartItem")
     List<OrderItemVo> getCurrentUserItems();
}
