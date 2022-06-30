package com.ypf.gulimall.cart.controller;

import com.ypf.common.constant.AuthServerConstant;
import com.ypf.gulimall.cart.Interctpor.CartInterceptor;
import com.ypf.gulimall.cart.service.CartService;
import com.ypf.gulimall.cart.vo.CartItemVo;
import com.ypf.gulimall.cart.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-24 23:38
 **/
@Controller
public class CartController {
    @Autowired
    CartService cartService;
@GetMapping("/currentUserCartItem")
@ResponseBody
    public List<CartItemVo> getCurrentUserItems(){
       return cartService.getUserCartItems();
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId) {
        cartService.deleteItem(skuId);
        return "redirect:http://1.117.229.165/cartService/cart.html";
    }

    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) {
        cartService.changeItemCount(skuId, num);
        return "redirect:http://1.117.229.165/cartService/cart.html";
    }

    @GetMapping("checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("check") Integer check) {
        cartService.checkItem(skuId, check);
        return "redirect:http://1.117.229.165/cartService/cart.html";

    }

    @GetMapping("/cart.html")

    public String CartListPage(HttpSession session, Model model) throws ExecutionException, InterruptedException {
        CartVo cartVo = cartService.getCart();
//        CartInterceptor.threadLocal.get();
        model.addAttribute("cart", cartVo);
        return "cartList";
    }

    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, Model model, RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {

        CartItemVo cartItemVo = cartService.addToCart(skuId, num);

        redirectAttributes.addAttribute("skuId", skuId);
        return "redirect:http://1.117.229.165/cartService/addToCartSuccess.html";
//        return "success";
    }

    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccess(@RequestParam("skuId") Long skuId, Model model) {
        //
        CartItemVo cartItemVo = cartService.getCartItem(skuId);
        model.addAttribute("item", cartItemVo);
        return "success";
    }
}
