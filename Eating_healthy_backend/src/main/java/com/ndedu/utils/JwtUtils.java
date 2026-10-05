package com.ndedu.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(Long userId, String username){
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId",userId);
        claims.put("username",username);
        return Jwts.builder()
                .claims(claims) //设置自定义载荷
                .subject(username) //设置主题
                .issuedAt(new Date()) //设置签发时间
                .expiration(new Date(System.currentTimeMillis() + expiration)) //设置过期时间
                .signWith(getKey()) //设置签名算法
                .compact();
    }

    //生成签名密钥
    private Key getKey(){
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
