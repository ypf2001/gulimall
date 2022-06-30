package com.ypf.gulimall.product.web;

import com.ypf.gulimall.product.service.SkuInfoService;
import com.ypf.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-20 14:40
 **/
@Controller
public class itemController {

    @Autowired
    SkuInfoService skuInfoService;
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo =  skuInfoService.item(skuId);
        System.out.println(skuItemVo);
        System.out.println(skuItemVo.getSaleAttr());
        model.addAttribute("item",skuItemVo);
        return "item";
    }
}
