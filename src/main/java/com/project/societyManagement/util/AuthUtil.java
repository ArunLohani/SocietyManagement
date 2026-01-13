package com.project.societyManagement.util;

import com.project.societyManagement.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import com.project.societyManagement.entity.Role;

@Component
public class AuthUtil {

    @Value("${jwt-secret}")
    private String jwtSecretKey;

    @Value("${jwt.expiration.ms}")
    private int jwtExpirationMs;

    @Value("${jwt.impersonation.expiration.ms:86400000}") // Default 24 hours
    private int jwtImpersonationExpirationMs;

    public SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String getAccessToken(User user){
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getRole)
                .collect(Collectors.toList());
        String rolesString = String.join(",", roleNames);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("tenantId", user.getTenant() != null ? user.getTenant().getId() : null)
                .claim("roles", rolesString)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * Generates an impersonation token that includes both admin and super admin info
     */
    public String getImpersonationToken(User admin, User superAdmin, Long sessionId){
        List<String> roleNames = admin.getRoles().stream()
                .map(Role::getRole)
                .collect(Collectors.toList());
        String rolesString = String.join(",", roleNames);

        return Jwts.builder()
                .subject(admin.getId().toString())
                .claim("email", admin.getEmail())
                .claim("tenantId", admin.getTenant() != null ? admin.getTenant().getId() : null)
                .claim("roles", rolesString)
                .claim("isImpersonation", true)
                .claim("superAdminId", superAdmin.getId())
                .claim("superAdminEmail", superAdmin.getEmail())
                .claim("sessionId", sessionId)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtImpersonationExpirationMs))
                .signWith(getSecretKey())
                .compact();
    }

    public String getEmailFromToken(String token){
        Claims claims = Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("email", String.class);
    }

    public Long getTenantIdFromToken(String token){
        Claims claims = Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("tenantId", Long.class);
    }

    public boolean isImpersonationToken(String token){
        Claims claims = Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Boolean isImpersonation = claims.get("isImpersonation", Boolean.class);
        return isImpersonation != null && isImpersonation;
    }

    public Long getSessionIdFromToken(String token){
        Claims claims = Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("sessionId", Long.class);
    }

    public String getSuperAdminEmailFromToken(String token){
        Claims claims = Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("superAdminEmail", String.class);
    }

    public Long getSuperAdminIdFromToken(String token){
        Claims claims = Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("superAdminId", Long.class);
    }
}