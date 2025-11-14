package com.github.myazusa.posthorseclouddelivery.core.utils;

import com.github.myazusa.posthorseclouddelivery.core.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class JwtUtils {
    private static final byte[] secretKey = "sawnvklkanlvkyuiysfodsfokklcanoicwejciojweoijakdhfdoswe".getBytes();

    // 生成token
    public static String generateToken(String phone, UUID uuid) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uuid", uuid);
        // todo：设置成spring配置
        long expirationTime = 1000L * 60 * 60 * 24 * 10;
        return Jwts.builder()
                .claims(claims)
                .subject(phone)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(Keys.hmacShaKeyFor(secretKey))
                .compact();
    }

    private static Claims parseToken(String header) {
        // 尝试解析
        try {
            return getClaims(header);
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    private static Claims getClaims(String header) {
        String s = header.replace("Bearer ", "");
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secretKey))
                .build().parseSignedClaims(s).getPayload();
    }

    // 获取token的手机信息
    public static String extractPhone(String header) {
        return parseToken(header).getSubject();
    }

    // 获取token的uuid信息
    public static UUID extractUUID(String header){
        return getClaims(header).get("uuid", UUID.class);
    }

    // 校验token日期
    public static boolean isTokenExpired(String header) {
        return parseToken(header).getExpiration().before(new Date());
    }

}
