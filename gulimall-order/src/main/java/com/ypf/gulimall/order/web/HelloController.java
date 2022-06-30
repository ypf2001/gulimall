package com.ypf.gulimall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-27 17:26
 **/
@Controller
public class HelloController {
    @GetMapping({"/{page}.html"})
    public String page(@PathVariable("page") String page) {
        return page;
    }


}
