package com.project.societyManagement.service;

import com.project.societyManagement.dto.Auth.Request.LoginRequest;
import com.project.societyManagement.dto.Auth.Request.RegisterRequest;
import com.project.societyManagement.dto.Auth.Response.AuthTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface AuthService {
    public AuthTokenResponse login(LoginRequest loginRequest);
    public AuthTokenResponse register(RegisterRequest registerRequest);

}
