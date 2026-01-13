package com.project.societyManagement.filter;

import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.config.ImpersonationAuthenticationToken;
import com.project.societyManagement.service.CustomUserDetailService;
import com.project.societyManagement.service.ImpersonationSessionService;
import com.project.societyManagement.util.AuthUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final AuthUtil authUtil;
    private final CustomUserDetailService userDetailService;
    private final ImpersonationSessionService impersonationSessionService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String accessToken = null;
            String impersonationToken = null;

            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("access_token".equals(cookie.getName())) {
                        accessToken = cookie.getValue();
                    }
                    if ("impersonation_token".equals(cookie.getName())) {
                        impersonationToken = cookie.getValue();
                    }
                }
            }

            // 🔹 Prefer impersonation token
            if (impersonationToken != null) {
                try {
                    authenticateImpersonation(impersonationToken);
                } catch (SecurityException ex) {
                    log.warn("Impersonation session ended, reverting to normal session");

                    clearImpersonationCookie(response);
                    SecurityContextHolder.clearContext();

                    if (accessToken != null) {
                        authenticateNormal(accessToken);
                    }
                }
            }
            // 🔹 Normal login
            else if (accessToken != null) {
                authenticateNormal(accessToken);
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }

    // ===================== NORMAL AUTH =====================

    private void authenticateNormal(String token) {

        String email = authUtil.getEmailFromToken(token);
        Long tenantId = authUtil.getTenantIdFromToken(token);

        TenantContextHolder.setCurrentTenant(tenantId);

        User user = (User) userDetailService.loadUserByUsername(email);

        ImpersonationAuthenticationToken authentication =
                new ImpersonationAuthenticationToken(
                        user,
                        user.getAuthorities(),
                        false,
                        null,
                        null
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("Authenticated normal user: {}", email);
    }

    // ===================== IMPERSONATION AUTH =====================

    private void authenticateImpersonation(String token) {

        Long sessionId = authUtil.getSessionIdFromToken(token);

        if (!impersonationSessionService.isSessionActiveWithoutAuth(sessionId)) {
            throw new SecurityException("Impersonation session expired or terminated");
        }

        String adminEmail = authUtil.getEmailFromToken(token);
        String superAdminEmail = authUtil.getSuperAdminEmailFromToken(token);
        Long tenantId = authUtil.getTenantIdFromToken(token);

        TenantContextHolder.setCurrentTenant(tenantId);

        User admin = (User) userDetailService.loadUserByUsername(adminEmail);

        ImpersonationAuthenticationToken authentication =
                new ImpersonationAuthenticationToken(
                        admin,
                        admin.getAuthorities(),
                        true,
                        superAdminEmail,
                        sessionId
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("SuperAdmin {} impersonating Admin {}", superAdminEmail, adminEmail);
    }

    // ===================== COOKIE CLEANER =====================

    private void clearImpersonationCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("impersonation_token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
