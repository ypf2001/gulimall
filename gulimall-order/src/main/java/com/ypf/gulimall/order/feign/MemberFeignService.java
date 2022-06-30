package com.ypf.gulimall.order.feign;

import com.ypf.gulimall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-28 16:06
 **/
@FeignClient("gulimall-member")
public interface MemberFeignService {
    @GetMapping({"/member/memberreceiveaddress/{memberId}/address"})
     List<MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId);

}
