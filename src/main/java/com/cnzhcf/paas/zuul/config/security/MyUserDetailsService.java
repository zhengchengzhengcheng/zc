package com.cnzhcf.paas.zuul.config.security;

import com.alibaba.fastjson.TypeReference;
import com.cnzhcf.paas.commons.util.FastJsonUtil;
import com.cnzhcf.paas.commons.util.MyException;
import com.cnzhcf.paas.commons.util.ZhJsonResult;
import com.cnzhcf.paas.user.domain.UserInfo;
import com.cnzhcf.paas.user.feign.UserInfoFeign;
import com.cnzhcf.paas.zuul.entity.MyUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserInfoFeign userInfoFeign;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUserInfo userInfo = null;
        UserInfo loginParam = new UserInfo();
        loginParam.setLoginName(username);
        ZhJsonResult result  = userInfoFeign.login(loginParam);
        if(!result.isStatus()){
           throw new MyException(result.getMsg());
        }
        userInfo = FastJsonUtil.map2Bean((Map) result.getData(), new TypeReference<MyUserInfo>() {});
        return userInfo;
    }
}
