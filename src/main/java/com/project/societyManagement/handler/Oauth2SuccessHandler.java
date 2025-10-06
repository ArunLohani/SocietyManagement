package com.project.societyManagement.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.Auth.Response.AuthTokenResponse;
import com.project.societyManagement.service.AuthService;
import com.project.societyManagement.service.impl.OAuth2ServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private OAuth2ServiceImpl oAuth2Service;
    private final ObjectMapper objectMapper;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        AuthTokenResponse authTokenResponse = oAuth2Service.handleOauth2LoginRequest(oAuth2User);

        ApiResponse<AuthTokenResponse> apiResponse = new ApiResponse<AuthTokenResponse>(true, "Login Successfull", authTokenResponse);
        ResponseEntity<ApiResponse> oauth2LoginResponse = new ResponseEntity<>(apiResponse, HttpStatus.OK);
        response.setStatus(oauth2LoginResponse.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(oauth2LoginResponse.getBody()));
    }
}
