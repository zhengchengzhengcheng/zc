package com.cnzhcf.paas.zuul.config.security;

import com.cnzhcf.paas.zuul.config.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Value("${redirect.path}")
    private String redirectPath;
 /*   @Autowired
    private MyFilterSecurityInterceptor myFilterSecurityInterceptor;
*/
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder());//添加自定义的userDetailsService认证
    }

    // 装载BCrypt密码编码器
    @Bean
    public Md5PasswordEncoder passwordEncoder() {
        return new Md5PasswordEncoder();
    }


    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public GoAuthenticationSuccessHandler authenticationSuccessHandler(){
        return new GoAuthenticationSuccessHandler();
    }

    @Bean
    public GoAuthenticationFailureHandler authenticationFailureHandler(){
        return new GoAuthenticationFailureHandler();
    }

    @Bean
    public GoLogoutSuccessHandler logoutSuccessHandler(){
        return new GoLogoutSuccessHandler();
    }

    @Bean
    public GoAccessDeniedHandler accessDeniedHandler(){
        return new GoAccessDeniedHandler();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 使用 JWT，关闭token
                .and()
                .httpBasic()
                // 未经过认证的用户访问受保护的资源
                .authenticationEntryPoint(new GoAuthenticationEntryPoint())
                .and()
                .authorizeRequests()
                //处理跨域请求中的Preflight请求
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/user/login", "/auth/**", "/zh_carloan/static/**", "/document/nauth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage(redirectPath) //重定向地址
                .loginProcessingUrl("/user/login")//登录地址
                .successHandler(authenticationSuccessHandler())//认证成功
                .failureHandler(authenticationFailureHandler())// 认证失败
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/user/logout")//退出
                .logoutSuccessHandler(logoutSuccessHandler())
                .permitAll()
                .and()
                .headers()
                .frameOptions().disable();
        // 记住我
       /* http.rememberMe().rememberMeParameter("remember-me")
                .userDetailsService(userDetailsService).tokenValiditySeconds(300);
*/
        http.exceptionHandling()
                // 已经认证的用户访问自己没有权限的资源处理
                .accessDeniedHandler(accessDeniedHandler())
                .and().addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
      //  http.addFilterBefore(myFilterSecurityInterceptor, FilterSecurityInterceptor.class);
    }


}
