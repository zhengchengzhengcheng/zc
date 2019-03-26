package com.cnzhcf.paas.zuul.config.auth;

import com.alibaba.fastjson.JSONArray;
import com.cnzhcf.paas.commons.beanutil.RedisUtil;
import com.cnzhcf.paas.commons.util.FastJsonUtil;
import com.cnzhcf.paas.commons.util.ZhJsonResult;
import com.cnzhcf.paas.user.feign.ResourceInfoFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 资源数据定义类
 */
@Service("securityMetadataSource")
public class MySecurityMetadataSourceService implements FilterInvocationSecurityMetadataSource {

    @Autowired
    private ResourceInfoFeign resourceInfoFeign;

    @Autowired
    private RedisUtil redisUtil;

    //缓存资源key
    private static final String ROLE_RESOURCE_KEY = "ROLE_RESOURCE_KEY";

    // 资源数据定义，将所有的资源和权限对应关系建立起来，即定义某一资源可以被哪些角色访问
    //getAttributes(Object o)方法返回的集合最终会来到AccessDecisionManager类中
    //加载当前请求（url） 需要什么角色才能访问
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        //从缓存中获取
        Map<String, Object> map = getResourceRoleMap();
        //object 中包含用户请求的request 信息
        HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
        AntPathRequestMatcher matcher;
        String resUrl = null;
        for(Iterator<String> iter = map.keySet().iterator(); iter.hasNext(); ) {
            resUrl = iter.next();
            matcher = new AntPathRequestMatcher(resUrl);
            if(matcher.matches(request)) {
                Object value = map.get(resUrl);
                JSONArray json = null;
                if(value instanceof JSONArray){
                     json = (JSONArray)map.get(resUrl);
                }
                if(value instanceof ArrayList){
                     json =  JSONArray.parseArray(JSONArray.toJSONString(value));
                }
                return SecurityConfig.createList(json.toArray(new String[]{}));
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    /**
     * 获取资源角色信息
     * @return
     */
    private Map<String, Object> getResourceRoleMap(){
        Map<String, Object> result = null;
        String str = (String)redisUtil.get(ROLE_RESOURCE_KEY);
        if(null == str){
            ZhJsonResult<Map<String, Object>>  resourceRoles = resourceInfoFeign.findResourceRole();
            result = resourceRoles.getData();
        }else{
            result = (Map<String, Object>) FastJsonUtil.json2Map(str);
        }
        return result;
    }

}
