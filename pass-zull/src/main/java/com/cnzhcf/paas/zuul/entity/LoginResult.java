package com.cnzhcf.paas.zuul.entity;

import java.io.Serializable;
import java.util.List;

public class LoginResult implements Serializable {
    private Long id;
    private Long geoId;
    private String token;
    private String loginName;
    private String name;
    private String phone;
    private Long companyId;
    private String companyName;
    private List<String> domIds;
    private List<String> roles;
    private List<MyRoleInfo> roleInfoList;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getDomIds() {
        return domIds;
    }

    public void setDomIds(List<String> domIds) {
        this.domIds = domIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGeoId() {
        return geoId;
    }

    public void setGeoId(Long geoId) {
        this.geoId = geoId;
    }

    public List<MyRoleInfo> getRoleInfoList() {
        return roleInfoList;
    }

    public void setRoleInfoList(List<MyRoleInfo> roleInfoList) {
        this.roleInfoList = roleInfoList;
    }
}
