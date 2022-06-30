package com.ypf.gulimall.search.controller;

import com.ypf.common.exception.BizCodeEnume;
import com.ypf.common.to.es.SkuEsModel;
import com.ypf.common.utils.R;
import com.ypf.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-11 00:03
 **/
@RestController
@RequestMapping("/search/save")
@Slf4j
public class ElasticSaveController {
    @Autowired
    ProductSaveService productSaveService;
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels){
     if( productSaveService.productStatusUp(skuEsModels)){
         return R.ok();
     }else {
         log.error("elasticsearch商品上架错误{}");
         return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnume.VAILD_EXCEPTION.getMsg());
     }


 }
 @ResponseBody
 @GetMapping({"/"})
 public String test (){
        return "hello";
 }
}
