package com.jztey.demo.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.jztey.demo.service.CustomUserService;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {// 1

	@Bean
	UserDetailsService customUserService() { // 2//
												// 注册CustomUserService的Bean,需要进行拦截的bean
		return new CustomUserService();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customUserService()); // 3// 添加我们自定义的user detail
		// service认证
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().authenticated() // 所有请求需要认证即登陆才能访问，只是对上面进行bean进行拦截，其他的不用拦截
				.and().formLogin().loginPage("/login").failureUrl("/login?error").permitAll() // 5定制登陆行为，登陆页面可以任意访问
				.and().logout().permitAll(); // 6定制注销的行为，注销请求可以任意访问

	}

}
