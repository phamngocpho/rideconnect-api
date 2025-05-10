package com.rideconnect.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collection;

public class LoginPageFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Kiểm tra xem đường dẫn có phải là trang login không
        String requestURI = httpRequest.getRequestURI();

        // Kiểm tra nếu là trang login và người dùng đã đăng nhập
        if ((requestURI.equals("/login") || requestURI.equals("/auth/login")) && isAuthenticated()) {
            String targetUrl = determineTargetUrl();
            httpResponse.sendRedirect(targetUrl);
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Kiểm tra xem người dùng đã đăng nhập chưa
     */
    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        // Kiểm tra xem authentication có phải là AnonymousAuthenticationToken không
        return authentication.isAuthenticated() &&
                !authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ANONYMOUS"));
    }

    /**
     * Xác định URL chuyển hướng dựa trên vai trò người dùng
     */
    private String determineTargetUrl() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return "/admin";
            } else if (authority.getAuthority().equals("ROLE_DRIVER")) {
                return "/driver";
            } else if (authority.getAuthority().equals("ROLE_CUSTOMER")) {
                return "/customer";
            }
        }

        return "/"; // Mặc định chuyển hướng về trang chủ
    }
}
