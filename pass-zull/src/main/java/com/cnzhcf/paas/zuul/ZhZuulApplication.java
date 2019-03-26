package com.cnzhcf.paas.zuul;

import com.cnzhcf.paas.zuul.config.dynamicrouter.ZuulRouteLocator;
import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

@EnableZuulProxy
@SpringBootApplication
@ComponentScan("com.cnzhcf.paas")
@EnableFeignClients("com.cnzhcf.paas.user.feign")
@EnableEurekaClient
public class ZhZuulApplication {



	public static void main(String[] args) {
		SpringApplication.run(ZhZuulApplication.class, args);
	}


	/*@Profile("!cloud")
	@Bean
	RequestDumperFilter requestDumperFilter() {
		return new RequestDumperFilter();
	}*/



}
