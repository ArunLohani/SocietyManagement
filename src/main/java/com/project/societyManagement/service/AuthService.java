package com.project.societyManagement.service;

import com.project.societyManagement.dto.Auth.Request.LoginRequest;
import com.project.societyManagement.dto.Auth.Request.RegisterRequest;
import com.project.societyManagement.dto.Auth.Response.AuthTokenResponse;

public interface AuthService {

    public AuthTokenResponse login(LoginRequest loginRequest);

    public AuthTokenResponse register(RegisterRequest registerRequest);


}
