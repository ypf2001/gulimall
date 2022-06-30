package com.ypf.gulimall.product.web;

import com.ypf.gulimall.product.entity.CategoryEntity;
import com.ypf.gulimall.product.service.CategoryService;
import com.ypf.gulimall.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-12 16:21
 **/

@Controller
public class indexController {
    @Autowired
    CategoryService categoryService;

    @GetMapping({"/", "index.html"})
    public String indexPage(Model model) {
        //TODO  查出所有意义分类
        List<CategoryEntity> level1Categorys = categoryService.getLevel1Categorys();
        model.addAttribute("categories", level1Categorys);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson(){
        Map<String, List<Catelog2Vo>> catalogJson = categoryService.getCatelogJson();
        return catalogJson;
    }

}
