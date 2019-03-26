package com.cnzhcf.paas.zuul.config.security;


import com.cnzhcf.paas.commons.util.FastJsonUtil;
import com.cnzhcf.paas.commons.util.ZhJsonResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: zhanglei.
 * @Description: 未登录
 * @Date:Created in 2018/8/25 23:11.
 */
public class GoAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
//
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        ZhJsonResult jsonResult = new ZhJsonResult();
        response.setStatus(401);
        response.getWriter().write(FastJsonUtil.bean2Json(jsonResult.tail("未登录"), true));
        response.getWriter().flush();
    }

}
