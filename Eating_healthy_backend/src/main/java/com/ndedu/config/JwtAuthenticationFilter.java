package com.ndedu.config;

import com.ndedu.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
            if(jwtUtils.validateToken(tokenRes)){
                Long userId = jwtUtils.getUserId(tokenRes);
                String username = jwtUtils.getUsername(tokenRes);
                System.out.println(userId);
                System.out.println(username);
            }
        }
        chain.doFilter(request, response);
    }
}
