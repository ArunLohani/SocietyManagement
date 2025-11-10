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
    public SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String getAccessToken(User user){
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getRole) // Assuming Role has a getName() method
                .collect(Collectors.toList());
        String rolesString = String.join(",",roleNames);
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email",user.getEmail())
                .claim("tenantId", user.getTenant() != null ? user.getTenant().getId() : null)
                .claim("roles",rolesString)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(getSecretKey())
                .compact();
    }

    public  String getEmailFromToken(String token){
        Claims claims = Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("email" , String.class);
    }

    public Long getTenantIdFromToken(String token){

        Claims claims = Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("tenantId" , Long.class);

    }
}
