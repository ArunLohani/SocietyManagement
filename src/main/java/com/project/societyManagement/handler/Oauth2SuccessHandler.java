package com.project.societyManagement.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.Auth.Response.AuthTokenResponse;
import com.project.societyManagement.service.AuthService;
import com.project.societyManagement.service.impl.OAuth2ServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class Oauth2SuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler implements AuthenticationSuccessHandler {
    @Autowired
    private OAuth2ServiceImpl oAuth2Service;
    private final ObjectMapper objectMapper;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.debug("INSIDE OAUTH2 SUCCESS HANDLER");
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        AuthTokenResponse authTokenResponse = oAuth2Service.handleOauth2LoginRequest(oAuth2User);

        ApiResponse<AuthTokenResponse> apiResponse = new ApiResponse<AuthTokenResponse>(true, "Login Successfull", authTokenResponse);

        ResponseEntity<ApiResponse> oauth2LoginResponse = new ResponseEntity<>(apiResponse, HttpStatus.OK);
        ResponseCookie jwtCookie = ResponseCookie.from("access_token", authTokenResponse
                .getToken()).httpOnly(false)
                .secure(false).path("/") .maxAge(3600).build();
        response.addHeader(HttpHeaders.SET_COOKIE,jwtCookie.toString());

        String frontendRedirectUrl = "http://localhost:4200/login";

        getRedirectStrategy().sendRedirect(request, response, frontendRedirectUrl);
    }
}
