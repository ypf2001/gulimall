package com.ypf.gulimall.cart.service;

import com.ypf.gulimall.cart.vo.CartItemVo;
import com.ypf.gulimall.cart.vo.CartVo;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-24 23:36
 **/
public interface CartService {
    /**添加到购物车
     * @Param:
     * @return:
     */
    CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;
/**
 * @Param:
 * @return:
 * 获取购物项
 */
    CartItemVo getCartItem(Long skuId);

    CartVo getCart() throws ExecutionException, InterruptedException;

    void clearCart(String cartKey);

    void checkItem(Long skuId, Integer check);

    void changeItemCount(Long skuId, Integer num);

    void deleteItem(Long skuId);

    List<CartItemVo> getUserCartItems();
}
