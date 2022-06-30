package com.ypf.gulimall.order.interceptor;

import com.alibaba.nacos.common.utils.JacksonUtils;
import com.ypf.common.constant.AuthServerConstant;
import com.ypf.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-28 09:40
 **/
@Component
public class LoginUserInterceptor implements HandlerInterceptor {
    public static  ThreadLocal<MemberRespVo>  loginUser= new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MemberRespVo memberRespVo = JacksonUtils.
                toObj(JacksonUtils.toJson(request.getSession().getAttribute(AuthServerConstant.LOGIN_USER)), MemberRespVo.class);
        if (memberRespVo != null) {
            loginUser.set(memberRespVo);
            return true;
        } else {
            request.getSession().setAttribute("msg","请先进行登录");
            response.sendRedirect("http://1.117.229.165/authService/login.html");
            return false;
        }
    }
}
