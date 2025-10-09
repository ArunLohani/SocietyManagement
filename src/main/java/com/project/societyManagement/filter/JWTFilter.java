package com.project.societyManagement.filter;

import com.project.societyManagement.entity.User;
import com.project.societyManagement.service.CustomUserDetailService;
import com.project.societyManagement.util.AuthUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
@Slf4j
@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private AuthUtil util;
    @Autowired
    private CustomUserDetailService userDetailService;
    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String token = extractTokenFromCookie(request);
            if(token == null){
                filterChain.doFilter(request,response);
                return;
            }
            String email = util.getEmailFromToken(token);
            if (email!= null && SecurityContextHolder.getContext().getAuthentication() == null){
                User user = (User) userDetailService.loadUserByUsername(email);
             user.getAuthorities().stream().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()) );
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(user , null , user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            filterChain.doFilter(request,response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request,response,null,e);
        }
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
