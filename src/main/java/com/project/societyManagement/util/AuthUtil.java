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


        return Jwts.builder().subject(user.getEmail())
                .subject(user.getId().toString())
                .claim("email",user.getEmail())
                .claim("role",user.getRole().name())
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


}
