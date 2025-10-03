package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.Auth.Request.LoginRequest;
import com.project.societyManagement.dto.Auth.Request.RegisterRequest;
import com.project.societyManagement.dto.Auth.Response.AuthTokenResponse;
import com.project.societyManagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService service;

    AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> login(@RequestBody LoginRequest loginRequest) {
        AuthTokenResponse loginResponse = service.login(loginRequest);
        ApiResponse<AuthTokenResponse> apiResponse = new ApiResponse<AuthTokenResponse>(true, "Login Successfull", loginResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> register(@RequestBody RegisterRequest registerRequest) {
        AuthTokenResponse loginResponse = service.register(registerRequest);
        ApiResponse<AuthTokenResponse> apiResponse = new ApiResponse<AuthTokenResponse>(true, "Register Successfull", loginResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
