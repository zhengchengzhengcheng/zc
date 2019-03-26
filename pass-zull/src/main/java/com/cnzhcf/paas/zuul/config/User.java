package com.cnzhcf.paas.zuul.config;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class User implements UserDetails {

    private String username;
    private String password;
    private List<String> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(String role : roles){
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return authorities;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override //过期
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override  //锁定
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override //过期
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override //禁用
    public boolean isEnabled() {
        return true;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
