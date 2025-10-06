package com.project.societyManagement.service.impl;

import com.project.societyManagement.dto.Auth.Request.RegisterRequest;
import com.project.societyManagement.dto.Auth.Response.AuthTokenResponse;
import com.project.societyManagement.dto.User.UserDetails;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.service.AuthService;
import com.project.societyManagement.service.UserService;
import com.project.societyManagement.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2ServiceImpl {

    private final UserService userService;
    private final AuthUtil authUtil;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private String generateSecureRandomPassword() {

        return new SecureRandom().ints(20, 33, 122)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
    public AuthTokenResponse handleOauth2LoginRequest(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        User user = userService.findUserByEmail(email);
        AuthTokenResponse authTokenResponse = null;
        //Signup
        if (user == null) {
            String randomPassword = generateSecureRandomPassword();
                User registerUser = User.builder()
                        .email(email)
                        .name(name)
                        .password(passwordEncoder.encode(randomPassword))
                        .build();
                registerUser = userService.saveUser(registerUser);
                log.info("User Saved in the DB");
                log.info("Generating Auth Token...");
                String token = authUtil.getAccessToken(registerUser);
                log.info("Auth Token Generated...");
                UserDetails userDetails = modelMapper.map(registerUser,UserDetails.class);
                return new AuthTokenResponse(token,userDetails);
        }
        else {
            String token = authUtil.getAccessToken(user);
            UserDetails userDetails = modelMapper.map(user,UserDetails.class);
            authTokenResponse = new AuthTokenResponse(token,userDetails);
        }

        return authTokenResponse;

    }
}
