package com.cnzhcf.paas.zuul.config.security;


import com.cnzhcf.paas.commons.util.FastJsonUtil;
import com.cnzhcf.paas.commons.util.ZhJsonResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    protected final Log logger = LogFactory.getLog(this.getClass());
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
//
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        ZhJsonResult jsonResult = new ZhJsonResult();
        logger.debug("############################  401" + request.getRequestURI());
        logger.info("############################  401" + request.getRequestURI());
        logger.error("############################  401" + request.getRequestURI());
        response.setStatus(401);
        response.getWriter().write(FastJsonUtil.bean2Json(jsonResult.tail("未登录"), true));
        response.getWriter().flush();
    }

}
