package com.example.teamproject.domain.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // ===== 전체 요청 URL 구성 =====
        String scheme = request.getHeader("X-Forwarded-Proto") != null
                ? request.getHeader("X-Forwarded-Proto")
                : request.getScheme();

        String host = request.getHeader("X-Forwarded-Host") != null
                ? request.getHeader("X-Forwarded-Host")
                : request.getServerName();

        int port = request.getServerPort();
        String uri = request.getRequestURI();
        String query = request.getQueryString();

        String fullUrl = scheme + "://" + host
                + ((port != 80 && port != 443) ? ":" + port : "")
                + uri
                + (query != null ? "?" + query : "");

        String method = request.getMethod();
        String clientIp = request.getHeader("X-Forwarded-For") != null
                ? request.getHeader("X-Forwarded-For")
                : request.getRemoteAddr();

        // ===== 요청 로그 =====
        System.out.printf("[REQ →] %s %s (IP: %s)%n", method, fullUrl, clientIp);

        filterChain.doFilter(request, response);

        // ===== 응답 로그 =====
        int status = response.getStatus();
        System.out.printf("[← RES] %s %s → Status: %d%n", method, fullUrl, status);
    }
}
