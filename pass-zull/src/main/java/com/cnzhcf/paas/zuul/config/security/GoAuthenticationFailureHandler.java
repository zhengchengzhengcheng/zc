package com.cnzhcf.paas.zuul.config.security;

import com.cnzhcf.paas.commons.util.FastJsonUtil;
import com.cnzhcf.paas.commons.util.ZhJsonResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: zhanglei.
 * @Description:如果身份验证失败时调用
 * @Date:Created in 2018/8/25 23:01.
 */
public class GoAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        ZhJsonResult json = new ZhJsonResult();
        response.getWriter().write(FastJsonUtil.bean2Json(json.tail(exception.getMessage()), true));
        response.getWriter().flush();
        response.getWriter().close();
    }
}
