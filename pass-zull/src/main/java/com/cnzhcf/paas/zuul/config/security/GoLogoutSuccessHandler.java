package com.cnzhcf.paas.zuul.config.security;

import com.alibaba.fastjson.JSON;
import com.cnzhcf.paas.commons.util.FastJsonUtil;
import com.cnzhcf.paas.commons.util.ZhJsonResult;
import com.cnzhcf.paas.zuul.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GoLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String authToken = request.getHeader("token");
        if(authToken != null){
            jwtTokenUtil.del(authToken);
        }
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        ZhJsonResult jsonResult = new ZhJsonResult();
        jsonResult.of("退出成功");
        response.getWriter().write(FastJsonUtil.bean2Json(jsonResult, true));
        response.getWriter().flush();
        response.getWriter().close();
    }
}
