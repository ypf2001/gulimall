package com.ypf.gulimall.ware.exception;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-29 17:06
 **/
public class NoStockException extends RuntimeException {
    public NoStockException(Long skuId){
        super("商品id:"+skuId+"没有足够的库存");
    }

}
