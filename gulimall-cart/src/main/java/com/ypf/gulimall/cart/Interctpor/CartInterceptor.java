package com.ypf.gulimall.cart.Interctpor;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.ypf.common.constant.AuthServerConstant;
import com.ypf.common.constant.CartConstant;
import com.ypf.common.vo.MemberRespVo;
import com.ypf.gulimall.cart.to.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-24 23:50
 **/
@Component
public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();

        MemberRespVo member =   JacksonUtils.toObj(JacksonUtils.toJson(session.getAttribute(AuthServerConstant.LOGIN_USER)),MemberRespVo.class);
        if (member != null) {
            userInfoTo.setUserId(member.getId());
        } else {
            //user-key
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(true);
                }
            }
        }
        //没有临时用户一定分配临时用户
        if (StringUtils.isEmpty(userInfoTo.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }
        threadLocal.set(userInfoTo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
       if (!userInfoTo.isTempUser()){
           Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
           cookie.setDomain("1.117.229.165");
           cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);

           response.addCookie(cookie);
       }

    }
}
