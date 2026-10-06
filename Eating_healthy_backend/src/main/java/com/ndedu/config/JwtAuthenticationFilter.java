package com.ndedu.config;

import com.ndedu.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if(StringUtils.hasText(token) && token.startsWith("Bearer ")){
            String tokenRes = token.substring(7);
            try{
                if(jwtUtils.validateToken(tokenRes)){
                    Long userId = jwtUtils.getUserId(tokenRes);
                    String username = jwtUtils.getUsername(tokenRes);
                    System.out.println(userId);
                    System.out.println(username);
                    //构建spring security 认证对象
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
                    //将认证信息存入Spring Security上下文
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }else{
                    SecurityContextHolder.clearContext();
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=utf-8");
                    response.getWriter().write("{\"code\":401,\"message\":\"token无效或过期\"}");
                    return;
                }
            }catch(Exception e){
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write("{\"code\":401,\"message\":\"token认证失败\"}");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
