package com.cnzhcf.paas.zuul.config.filter;

import com.cnzhcf.paas.commons.beanutil.RedisUtil;
import com.cnzhcf.paas.commons.util.CookieUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
public class PreFilter extends ZuulFilter {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        //接口
        String sessionId = request.getHeader("token");
        String jwtToken = null;
        //web项目
        if(sessionId == null){
            sessionId = CookieUtils.getCookieValue(request, "cnzhcf.sessionId");
        }
        if(!StringUtils.isEmpty(sessionId)){
            Object obj = redisUtil.get(sessionId);
            if(!StringUtils.isEmpty(obj)){
                jwtToken = obj.toString();
            }
        }
        ctx.addZuulRequestHeader("token", jwtToken);
        ctx.addZuulRequestHeader("original_requestURL",request.getRequestURL().toString());
        return null;
    }
}
