package com.ndedu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final String[] PUBLIC_PATHS = {
            "/api/auth/test",
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                //禁用csrf(无状态JWT)
                .csrf(AbstractHttpConfigurer::disable)
                //配置会话管理策略
                .sessionManagement(session ->
                        //所有认证信息通过JWT传递
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //配置请求的授权规则
                //对PUBLIC_PATHS中的路径放行，允许所有用户访问（包括没有认证的用户）
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(PUBLIC_PATHS).permitAll());
                //构建返回实例，包含所有配置的安全过滤规则
                return http.build();
    }
}
