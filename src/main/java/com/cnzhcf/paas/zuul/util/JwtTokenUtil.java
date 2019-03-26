package com.cnzhcf.paas.zuul.util;


import com.cnzhcf.paas.commons.beanutil.RedisUtil;
import com.cnzhcf.paas.commons.util.TokenUtil;
import com.cnzhcf.paas.zuul.entity.MyUserInfo;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @Description:JWT工具类
 * @Date:Created in 2018/8/26 18:27.
 */
@Component
public class JwtTokenUtil implements Serializable {

    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";
    private static final long serialVersionUID = -8305152446124853696L;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 将token存储到redis
     */
    public void setExpire(String key, String val, long time) {
        redisUtil.set(key, val, time);
    }

    /**
     * 移除
     */
    public void del(String key) {
        redisUtil.del(key);
    }

    /**
     * 判断是否有效
     *
     * @param authToken
     * @return
     */
    public Boolean validateToken(String authToken) {
        Object o = redisUtil.get(authToken);
        if (null != o) {
            return true;
        }
        return false;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims getClaimsFromToken(String token) {
      return TokenUtil.parserToken(token, "2");
    }

    /**
     * 生成令牌
     *
     * @param userDetails 用户
     * @return 令牌
     */
    public String generateToken(MyUserInfo info) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, info.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        claims.put("USER_ID", info.getId());
        return TokenUtil.getToken(claims);
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = TokenUtil.parserToken(token, "2");
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 判断令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 刷新令牌
     * @param token 原令牌
     * @return 新令牌
     */
    public String refreshToken(String token) {
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token);
            claims.put(CLAIM_KEY_CREATED, new Date());
            refreshedToken = TokenUtil.getToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * 验证令牌
     * @param token       令牌
     * @param userDetails 用户
     * @return 是否有效
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        MyUserInfo user = (MyUserInfo) userDetails;
        String username = getUsernameFromToken(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
     //  return true;
    }
}
