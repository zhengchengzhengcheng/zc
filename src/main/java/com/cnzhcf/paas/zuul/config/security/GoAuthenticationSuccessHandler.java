package com.cnzhcf.paas.zuul.config.security;

import com.cnzhcf.paas.commons.util.CookieUtils;
import com.cnzhcf.paas.commons.util.FastJsonUtil;
import com.cnzhcf.paas.commons.util.ZhJsonResult;
import com.cnzhcf.paas.zuul.entity.LoginResult;
import com.cnzhcf.paas.zuul.entity.MyRoleInfo;
import com.cnzhcf.paas.zuul.entity.MyUserInfo;
import com.cnzhcf.paas.zuul.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class GoAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private Environment env;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //返回登录信息
        MyUserInfo info = (MyUserInfo)userDetails;
        //产生sessionid 使用uuid
        String sessionId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        info.setPassword(null);
        info.setToken(sessionId);
        info.setDefaultPassword(null);
        //写入redis
        setJWtToRedis(request, response, info, sessionId);
        //单独处理返回登录信息--避免敏感信息
        ZhJsonResult jsonResult = new ZhJsonResult();
        LoginResult result = new LoginResult();
        BeanUtils.copyProperties(info, result);
        setRoleInfo(info, result);
        jsonResult.of(result);
        response.getWriter().write(FastJsonUtil.bean2Json(jsonResult, true));
        response.getWriter().flush();
    }


    private void setJWtToRedis(HttpServletRequest request, HttpServletResponse response, MyUserInfo info, String sessionId){
        //获取登录来源
        String source = request.getParameter("source");
        if(StringUtils.isEmpty(source)){
            source = "web";
        }
        //从配置中获取登录过期时间
        Long expiration = Long.valueOf(env.getProperty("jwt.expiration."+ source));
        //生成token
        String jwtToken = jwtTokenUtil.generateToken(info);
        log.info("################### jwtToken" + jwtToken);
        //将用户信息写入缓存
        jwtTokenUtil.setExpire(jwtToken, FastJsonUtil.bean2Json(info), expiration);
        jwtTokenUtil.setExpire(sessionId, jwtToken, expiration);
        //写入cookie -- 老车贷集成
        if(source.equals("web")){
            CookieUtils.setCookie(request, response, "cnzhcf.sessionId", sessionId, Integer.valueOf(expiration.toString()));
        }
    }

    private void setRoleInfo(MyUserInfo info, LoginResult result){
        List<MyRoleInfo> myRoleInfos = new ArrayList<MyRoleInfo>();
        info.getRoleInfoList().stream().forEach(temp ->{
            MyRoleInfo myRoleInfo = new MyRoleInfo();
            BeanUtils.copyProperties(temp, myRoleInfo);
            myRoleInfos.add(myRoleInfo);
        });
        result.setRoleInfoList(myRoleInfos);
    }

}
