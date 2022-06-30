package com.ypf.gulimall.search.controller;

import com.alibaba.nacos.common.utils.JacksonUtils;
import com.ypf.gulimall.search.service.MallSearchService;
import com.ypf.gulimall.search.vo.SearchParam;
import com.ypf.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-16 14:24
 **/
@Controller
public class SearchController {
    @Autowired
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model, HttpServletRequest request) {
        searchParam.set_queryString(request.getQueryString());
        SearchResult results = mallSearchService.search(searchParam);
        // System.out.println(results.size());
        model.addAttribute("result", results);
        return "list";
    }
}
