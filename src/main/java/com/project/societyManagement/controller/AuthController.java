package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.Auth.Request.LoginRequest;
import com.project.societyManagement.dto.Auth.Request.RegisterRequest;
import com.project.societyManagement.dto.Auth.Response.AuthTokenResponse;
import com.project.societyManagement.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@Slf4j
//@CrossOrigin(value = "http://localhost:4200/")
public class AuthController {
    private final AuthService service;

    AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> login(@RequestBody LoginRequest loginRequest , HttpServletResponse response) {
        log.info("Request received for /login endpoint.");
        AuthTokenResponse loginResponse = service.login(loginRequest,response);
        ApiResponse<AuthTokenResponse> apiResponse = new ApiResponse<AuthTokenResponse>(true, "Login Successfull", loginResponse);
        log.info("Response Generated : Login Successful");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> register(@RequestBody RegisterRequest registerRequest ,  HttpServletResponse response) {
        log.info("Request received for /register endpoint.");
        AuthTokenResponse loginResponse = service.register(registerRequest,response);
        ApiResponse<AuthTokenResponse> apiResponse = new ApiResponse<AuthTokenResponse>(true, "Register Successfull", loginResponse);
        log.info("Response Generated : Register Successful");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }



}
