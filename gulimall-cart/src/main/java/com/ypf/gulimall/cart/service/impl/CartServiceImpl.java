package com.ypf.gulimall.cart.service.impl;

import com.alibaba.nacos.common.utils.JacksonUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ypf.common.utils.R;
import com.ypf.gulimall.cart.Interctpor.CartInterceptor;
import com.ypf.gulimall.cart.feign.ProductFeignService;
import com.ypf.gulimall.cart.service.CartService;
import com.ypf.gulimall.cart.to.SkuInfoTo;
import com.ypf.gulimall.cart.to.UserInfoTo;
import com.ypf.gulimall.cart.vo.CartItemVo;
import com.ypf.gulimall.cart.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Console;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-24 23:35
 **/
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    StringRedisTemplate redisTemplate;
    private final String CART_PREFIX = "gulimall:cart:";
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    /**购物车的添加
     *@Param: a
    * @return: a
     */
    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {


        BoundHashOperations<String, Object, Object> cartOps = extracted();
        String str = (String) cartOps.get(skuId.toString());

        if (StringUtils.isEmpty(str)) {
            CartItemVo cartItemVo = new CartItemVo();
            //远程查询
            R info = productFeignService.info(skuId);

            CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
                SkuInfoTo skuInfo = (SkuInfoTo) info.getData("skuInfo", new TypeReference<SkuInfoTo>() {
                });
                //商品信息
                cartItemVo.setCheck(true);
                cartItemVo.setCount(num);
                cartItemVo.setImage(skuInfo.getSkuDefaultImg());
                cartItemVo.setTitle(skuInfo.getSkuTitle());
                cartItemVo.setPrice(skuInfo.getPrice());
                cartItemVo.setSkuId(skuId);
            }, threadPoolExecutor);

            CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItemVo.setSkuAttrValues(skuSaleAttrValues);
            }, threadPoolExecutor);
            CompletableFuture.allOf(future1, voidCompletableFuture).get();
            String s = JacksonUtils.toJson(cartItemVo);
            cartOps.put(skuId.toString(), s);
            return cartItemVo;
        } else {
            CartItemVo cartItemVo = JacksonUtils.toObj(str, CartItemVo.class);
            cartItemVo.setCount(cartItemVo.getCount() + num);
            cartOps.put(skuId.toString(), JacksonUtils.toJson(cartItemVo));
            return cartItemVo;
        }


    }

    @Override
    public CartItemVo getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = extracted();
        CartItemVo cartItemVo = JacksonUtils.toObj(cartOps.get(skuId.toString()).toString(), CartItemVo.class);

        return cartItemVo;
    }

    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {
        CartVo cartVo = new CartVo();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() != null) {
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            String tempCartKey=CART_PREFIX+userInfoTo.getUserKey();
//            BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
            List<CartItemVo> cartItems = getCartItems(tempCartKey);
            if (!CollectionUtils.isEmpty(cartItems)) {
                for (CartItemVo item : cartItems) {
                    addToCart(item.getSkuId(), item.getCount());
                }
                //清零时购物车数据
                clearCart(tempCartKey);
            }
            //数据合并
            List<CartItemVo> cartItems1 = getCartItems(cartKey);
            cartVo.setItems(cartItems1);

        } else {
            //没登陆
            String cartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);


        }
        return cartVo;
    }

    @Override
    public void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> extracted = extracted();
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCheck(check==1?true:false);
        String s = JacksonUtils.toJson(cartItem);
        extracted.put(skuId.toString(),s);
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> extracted = extracted();
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        extracted.put(skuId.toString(),JacksonUtils.toJson(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> extracted = extracted();

        extracted.delete(skuId.toString());
    }

    @Override
    public List<CartItemVo> getUserCartItems() {
        UserInfoTo userInfoTo  = CartInterceptor.threadLocal.get();
        if(userInfoTo==null){
            return null;
        }else{
            List<CartItemVo> cartItems = getCartItems(CART_PREFIX+userInfoTo.getUserId());
            List<CartItemVo> collect = cartItems.stream().filter(item -> item.getCheck())
                    .map(item -> {
                        //更新最新价格
                        R price = productFeignService.getPrice(item.getSkuId());
                        String data =String.valueOf(price.get("data"))  ;
                        item.setPrice(new BigDecimal(data));
                        return item;
                    })
                    .collect(Collectors.toList());
            return collect;
        }

    }

    /**绑定redis字段抽离
     * @Param:
     * @return:
     */
    private BoundHashOperations<String, Object, Object> extracted() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations = redisTemplate.boundHashOps(cartKey);
        return stringObjectObjectBoundHashOperations;
    }

    private List<CartItemVo> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if (!CollectionUtils.isEmpty(values)) {
            List<CartItemVo> collect = values.stream().map((obj) -> {
                CartItemVo cartItemVo = JacksonUtils.toObj(obj.toString(), CartItemVo.class);
                return cartItemVo;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

}
