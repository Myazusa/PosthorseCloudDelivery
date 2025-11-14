package com.github.myazusa.posthorseclouddelivery.core.filter;

import com.github.myazusa.posthorseclouddelivery.core.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final static String AUTH_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 跳过不需要收到保护的登录验证接口
        if (request.getRequestURI().startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 解析token并判断时间是否过期
        var header = request.getHeader(AUTH_HEADER);
        if (header==null || JwtUtils.isTokenExpired(header)){
            return;
        }
        // 如果没有过期，解析token获取手机号
        var phone = JwtUtils.extractPhone(header);
        if (phone != null) {
            // 设置认证状态为true
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(phone, null, null);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 保存凭证到上下文以便使用
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 传递给下一个链
        filterChain.doFilter(request, response);
    }
}
