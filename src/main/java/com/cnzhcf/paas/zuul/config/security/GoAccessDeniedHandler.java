package com.cnzhcf.paas.zuul.config.security;

import com.cnzhcf.paas.commons.util.FastJsonUtil;
import com.cnzhcf.paas.commons.util.ZhJsonResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GoAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        ZhJsonResult jsonResult = new ZhJsonResult();
        response.setStatus(403);
        response.getWriter().write(FastJsonUtil.bean2Json(jsonResult.tail("无权访问！"), true));
        response.getWriter().flush();
    }

}
