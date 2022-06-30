package com.ypf.gulimall.order.web;

import com.alibaba.nacos.common.utils.JacksonUtils;
import com.ypf.gulimall.order.service.OrderService;
import com.ypf.gulimall.order.vo.OrderConfirmVo;
import com.ypf.gulimall.order.vo.OrderSubmitVo;
import com.ypf.gulimall.order.vo.SubmitOrderResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-28 09:36
 **/
@Slf4j
@Controller
public class OrderWebController {
    @Autowired
    OrderService orderService;

    @GetMapping({"/toTrade"})
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
       OrderConfirmVo confirmVo =  orderService.confirmOrder();
        model.addAttribute("orderConfirmData",confirmVo);
       log.info(JacksonUtils.toJson(confirmVo.getStocks()));
        return "confirm";
    }
@PostMapping("/submitOrder")
    public  String submitOrder(OrderSubmitVo orderSubmitVo, Model model, RedirectAttributes redirectAttributes)
    {
        //下单 创建 锁库存 验价格....
      SubmitOrderResponseVo responseVo =  orderService.submitOrder(orderSubmitVo);
      if(responseVo.getCode() ==0 ){
          //成功
        model.addAttribute("submitOrderResp",responseVo);
        return "pay";
      }else {
          String msg = "下单失败";
           switch (responseVo.getCode()){
               case 1:  msg+="订单信息过期 请刷新";break;
               case 2:  msg+="价格发送变化 请刷新";break;
               case 3:  msg+="库存锁定失败 商品不足";break;
           }
//           redirectAttributes.addFlashAttribute("msg",msg);
          return  "redirect:http://1.117.229.165/orderService/toTrade";
      }
    }
}
