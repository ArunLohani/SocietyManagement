package com.project.societyManagement.config;

import com.project.societyManagement.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public class ImpersonationAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final boolean impersonating;
    private final String superAdminEmail;
    private final Long sessionId;

    public ImpersonationAuthenticationToken(
            User principal,
            Collection<? extends GrantedAuthority> authorities,
            boolean impersonating,
            String superAdminEmail,
            Long sessionId
    ) {
        super(principal, null, authorities);
        this.impersonating = impersonating;
        this.superAdminEmail = superAdminEmail;
        this.sessionId = sessionId;
    }

    public boolean isImpersonating() {
        return impersonating;
    }

    public String getSuperAdminEmail() {
        return superAdminEmail;
    }

    public Long getSessionId() {
        return sessionId;
    }
}
