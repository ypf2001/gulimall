package com.ypf.gulimall.search.service;

import com.ypf.gulimall.search.vo.SearchParam;
import com.ypf.gulimall.search.vo.SearchResult;

import java.util.List;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-16 15:20
 **/
public interface MallSearchService {
SearchResult search(SearchParam param);
}
