package com.project.societyManagement.service;

import com.project.societyManagement.dto.Auth.Request.LoginRequest;
import com.project.societyManagement.dto.Auth.Request.RegisterRequest;
import com.project.societyManagement.dto.Auth.Response.AuthTokenResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    public AuthTokenResponse login(LoginRequest loginRequest , HttpServletResponse response);
    public AuthTokenResponse register(RegisterRequest registerRequest , HttpServletResponse response);
    public void logout(HttpServletResponse response);
}
