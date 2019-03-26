package com.cnzhcf.paas.zuul.config.filter;

import com.cnzhcf.paas.commons.beanutil.RedisUtil;
import com.cnzhcf.paas.commons.util.CookieUtils;
import com.cnzhcf.paas.commons.util.FastJsonUtil;
import com.cnzhcf.paas.commons.util.MyException;
import com.cnzhcf.paas.commons.util.ZhJsonResult;
import com.cnzhcf.paas.zuul.config.dynamicrouter.ZuulRouteLocator;
import com.cnzhcf.paas.zuul.util.JwtTokenUtil;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * jwt 拦截器
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ZuulProperties zuulProperties;

    @Autowired
    private ServerProperties server;

    @Value("${config.is_dev}")
    private boolean isDev;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            isDev(request);
            //跨域
            if (cors(request, response)){
                return;
            }
            if(isLogin(request)){
                filterChain.doFilter(request, response);
            }else {
                validateLogin(request, response, filterChain);
            }
        } catch (MyException e) {
            e.printStackTrace();
            resultJson(response, e.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
            resultJson(response, "系统异常，请联系管理员！");
        }
    }

    //是否调试模式
    private void isDev(HttpServletRequest request) {
        if(isDev){
            logger.info("############################ ip " + request.getHeader("x-forwarded-for"));
            logger.debug("############################ ip " + request.getHeader("x-forwarded-for"));
            logger.error("############################ ip " + request.getHeader("x-forwarded-for"));
            ZuulRouteLocator routeLocator = new ZuulRouteLocator(this.server.getServletPrefix(), this.zuulProperties, request.getHeader("x-forwarded-for"));
            routeLocator.refresh();
        }

    }


    private void resultJson(HttpServletResponse response, String message){
        try {
            response.setHeader("Content-Type", "application/json;charset=utf-8");
            ZhJsonResult jsonResult = new ZhJsonResult();
            response.getWriter().write(FastJsonUtil.bean2Json(jsonResult.tail(message), true));
            response.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否为登录
     * @param request
     * @return
     */
    private Boolean isLogin(HttpServletRequest request){
        boolean isLogin = false;
        String url = request.getRequestURI();
        if(!StringUtils.isEmpty(url) && url.contains("/user/login")){
            isLogin = true;
        }
        return isLogin;
    }

    /**
     * 验证登录
     * @param request
     * @param response
     */
    private void validateLogin(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //非web项目
            String url = request.getRequestURI();
            String authToken = request.getHeader("token");
            logger.info("###################################################### token" + authToken);
            String jwtToken = null;
            //web项目
            if(authToken == null){
                authToken = CookieUtils.getCookieValue(request, "cnzhcf.sessionId");
            }
            if(!StringUtils.isEmpty(authToken)){
                Object obj = redisUtil.get(authToken);
                if(!StringUtils.isEmpty(obj)){
                    jwtToken = obj.toString();
                }
            }
            if (jwtToken != null) {
                //String authToken = authHeader.substring("Bearer".length());
                String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if(jwtTokenUtil.validateToken(jwtToken)) {
                        if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        } else {
                            jwtTokenUtil.del(jwtToken);
                        }
                    }
                }
            }

        filterChain.doFilter(request, response);
    }


    /**
     * 跨域
     * @param request
     * @param response
     */
    private boolean cors(HttpServletRequest request, HttpServletResponse response) {
        if (request.getMethod().equals("OPTIONS")) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,OPTIONS,DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "Origin,X-Requested-With,Content-Type,Accept,Authorization,token");
            return true;
        }
        return false;
    }
}
