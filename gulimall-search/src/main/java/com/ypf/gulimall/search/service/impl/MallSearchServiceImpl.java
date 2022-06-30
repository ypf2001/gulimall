package com.ypf.gulimall.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.json.JsonData;
import com.alibaba.nacos.common.utils.StringUtils;
import com.ypf.common.to.es.SkuEsModel;
import com.ypf.gulimall.search.constant.EsConstant;
import com.ypf.gulimall.search.service.MallSearchService;
import com.ypf.gulimall.search.vo.SearchParam;
import com.ypf.gulimall.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-16 15:20
 **/
@Slf4j
@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Autowired
    ElasticsearchClient client;

    @Override
    public SearchResult search(SearchParam param) {
        System.out.println(param);
        /**
         * 模糊匹配
         */
        Query query = Query.of(fn1 -> {
            fn1.bool(fn2 -> {
                if (!StringUtils.isEmpty(param.getKeyword())) {
                    fn2.must(fn3 -> {
                        fn3.match(fn4 -> {
                            fn4.field("skuTitle").query(param.getKeyword());
                            return fn4;
                        });
                        return fn3;
                    });
                }
                fn2.filter(fn3 -> {
                    if (param.getCatalog3Id() != 0) {
                        fn3.term(fn4 -> {
                            fn4.field("catelogId").value(param.getCatalog3Id());

                            return fn4;
                        });
                    }
                    if (param.getBrandId() != null && param.getBrandId().size() > 0) {
                        fn3.terms(TermsQuery.of(fn4 -> {
                            fn4.field("brandId").terms(fn5 -> {
                                List<FieldValue> fieldValues = param.getBrandId().stream().map(item -> {
                                    FieldValue fieldValue = FieldValue.of(val -> {
                                        val.longValue(item);
                                    });
                                    return fieldValue;
                                }).collect(Collectors.toList());
                                fn5.value(fieldValues);
                                return fn5;
                            });

                            return fn4;
                        }));
                    }
                    if (param.getAttrs() != null && param.getAttrs().size() > 0 && !StringUtils.isEmpty(param.getAttrs().get(0))) {
                        fn3.nested(fn4 -> {
                            fn4.path("attrs");
                            param.getAttrs().forEach(item -> {
                                String[] s = item.split("_");
                                String attrId = s[0];
                                String[] attrValues = s[1].split(":");
                                fn4.query(fn5 -> {
                                    fn5.bool(fn6 -> {
                                        fn6.must(fn7 -> {
                                            fn7.term(fn8 -> {
                                                fn8.field("attr.attrId").value(attrId);
                                                return fn8;
                                            });
                                            fn7.terms(fn8 -> {
                                                fn8.field("attrs.attrValue").terms(fn9 -> {
                                                    List<FieldValue> fieldValues = Arrays.asList(attrValues).stream().map(attr -> {
                                                        FieldValue value = FieldValue.of(val -> {
                                                            val.stringValue(attr);
                                                        });
                                                        return value;
                                                    }).collect(Collectors.toList());

                                                    fn9.value(fieldValues);
                                                    return fn9;
                                                });
                                                return fn8;
                                            });
                                            return fn7;
                                        });
                                        return fn6;
                                    });
                                    return fn5;
                                });
                            });
                            return fn4;
                        });
                    }
                    if (param.getHasStock() != null) {
                        fn3.term(fn4 -> {
                            fn4.field("hasStock").value(param.getHasStock() == 0 ? false : true);
                            return fn4;
                        });
                    }
                    if (!StringUtils.isEmpty(param.getSkuPrice())) {
                        String[] price = param.getSkuPrice().split("_");
                        if (price.length == 2) {
                            fn3.range(fn4 -> {
                                fn4.lte(JsonData.of(price[1])).gte(JsonData.of(price[0]));
                                return fn4;
                            });
                        } else if (price.length == 1) {
                            if (param.getSkuPrice().startsWith("_")) {
                                fn3.range(fn4 -> {
                                    fn4.field("skuPrice")
                                            .lte(JsonData.of(price[1]));
                                    return fn4;
                                });
                            }
                            if (param.getSkuPrice().endsWith("_")) {
                                fn3.range(fn4 -> {
                                    fn4.field("skuPrice")
                                            .gte(JsonData.of(price[0]));
                                    return fn4;
                                });
                            }
                        }
                    }

                    return fn3;
                });
                return fn2;
            });
            return fn1;
        });
        SortOptions sortOptions = null;
        if (!StringUtils.isEmpty(param.getSort())) {
            String sort = param.getSort();
            String[] sortFileds = sort.split("_");
            SortOrder sortOrder = "asc".equalsIgnoreCase(sortFileds[1]) ? SortOrder.Asc : SortOrder.Desc;
            sortOptions = SortOptions.of(fn1 -> {
                fn1.field(FieldSort.of(
                        fn -> fn.field("skuPrice").order(sortOrder)
                ));
                return fn1;
            });

        }

/**
 *排序 分页 高亮
 */
        Highlight highlight = null;
        if (!StringUtils.isEmpty(param.getKeyword())) {
            highlight = Highlight.of(fn1 -> {
                fn1.fields("skuTitle", HighlightField.of(fn2 -> {
                    fn2.preTags("<b style='color: red'>").postTags("</b>");
                    return fn2;
                }));
                return fn1;
            });
        }

        /**
         * 聚合分析
         */
        Map<String, Aggregation> map = null;
        if(param.getAttrs()!=null&&param.getAttrs().size()>0){
           map = new HashMap<>();
            map.put("brand_agg", Aggregation.of(fn1 ->
                    fn1.terms(fn2 -> {
                                fn2.field("brandId").size(50);
                                return fn2;
                            })
                            .aggregations("brand_name_agg", Aggregation.of(fn2 ->
                                    fn2.terms(fn3 -> {
                                        fn3.field("brandName").size(1);
                                        return fn3;
                                    })
                            ))
                            .aggregations("brand_img_agg", fn2 ->
                                    fn2.terms(fn3 -> {
                                        fn3.field("brandImg")
                                                .size(1);
                                        return fn3;
                                    })
                            )
                            .aggregations("catelogAgg", fn2 ->
                                    fn2.terms(fn3 ->
                                                    fn3.field("catelogId").size(20)
                                            )
                                            .aggregations("catelogName", fn3 ->
                                                    fn3.terms(fn4 -> fn4.field("catelogName").size(1))

                                            )
                            )
                            .aggregations("attrAgg", fn2 ->
                                    fn2.nested(fn3 -> {
                                                fn3.path("attrs");
                                                return fn3;
                                            })
                                            .aggregations("attr_id_agg", fn3 ->
                                                    fn3.terms(fn4 -> {
                                                                fn4.field("attrs.attrId");
                                                                return fn4;

                                                            })
                                                            .aggregations("attr_name_agg", fn4 ->
                                                                    fn4.terms(fn5 -> {
                                                                        fn5.field("attrs.attrName").size(1);
                                                                        return fn5;
                                                                    })
                                                            ).
                                                            aggregations("attr_value_agg", fn4 ->
                                                                    fn4.terms(fn5 -> {
                                                                        fn5.field("attrs.attrValue").size(50);
                                                                        return fn5;
                                                                    }))
                                            ))));

        }

        SortOptions finalSortOptions = sortOptions;
        Highlight finalHighlight = highlight;
        Map<String, Aggregation> finalMap = map;

        SearchRequest request = SearchRequest.of(fn -> {
            if(finalMap!=null){
                fn.query(query)
                        .sort(finalSortOptions).highlight(finalHighlight)
                        .size(EsConstant.PRODUCT_PAGESIZE)
                        .from(param.getPageNum() > 0 && param.getPageNum() != null ? (param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE : 0)
                        .aggregations(finalMap).index(EsConstant.PRODUCT_INDEX);
                return fn;
            }else {
                fn.query(query)
                        .sort(finalSortOptions)
                       .highlight(finalHighlight)
                        .size(EsConstant.PRODUCT_PAGESIZE)
                        .from(param.getPageNum() > 0 && param.getPageNum() != null ? (param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE : 0)
                        .index(EsConstant.PRODUCT_INDEX);
                return fn;
            }
        });
        //SearchResult searchResult = buildSearchResult();
        SearchResponse<SkuEsModel> response = null;
        SearchResult results = new SearchResult();
        try {
            response = client.search(request, SkuEsModel.class);
            if(response.hits().hits().size()>0){
                List<SkuEsModel> finalResults = new ArrayList<>();
                response.hits().hits().forEach(item->{
                    item.highlight().get("skuTitle").forEach(v->{
                        item.source().setSkuTitle(v);

                    });
                    finalResults.add( item.source());
                });
                if(response.hits().total().value()>EsConstant.PRODUCT_PAGESIZE.longValue()){
                  Integer val =  new Long(response.hits().total().value()).intValue();
                    results.setTotalPages(val/16+1 );
                    System.out.println(val);
                }
                results.setProducts(finalResults);
            }
//response.hits().hits().
//            response.hits().hits().forEach(
//                    item-> System.out.println("响应结果为"+item.source().getAttrs())
//            );
//            buildSearchResult(response,param);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return results;
    }

//    private SearchResult buildSearchResult(SearchResponse response, SearchParam param) {
//
//        List<Hit<SearchResult>> hits = response.hits().hits();
//        List<SkuEsModel> skuEsModels = new ArrayList<>();
//        //遍历所有商品信息
//
//        return null;
//    }

    /**
     * 请求准备
     *
     * @Param:
     * @return:
     */
    private SearchResult buildSearchResult(SearchParam param) {


        return null;
    }
}
