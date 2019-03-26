package com.cnzhcf.paas.zuul.config.dynamicrouter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class ZullRouteConfig {

    @Autowired
    private ZuulProperties zuulProperties;

    @Autowired
    private ServerProperties server;

    @Bean
    public ZuulRouteLocator routeLocator() {
        ZuulRouteLocator routeLocator = new ZuulRouteLocator(this.server.getServletPrefix(), this.zuulProperties, "");
        //routeLocator.setJdbcTemplate(jdbcTemplate);
        return routeLocator;
    }

}
