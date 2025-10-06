package com.project.societyManagement.service.impl;

import com.project.societyManagement.dto.Auth.Request.LoginRequest;
import com.project.societyManagement.dto.Auth.Request.RegisterRequest;
import com.project.societyManagement.dto.Auth.Response.AuthTokenResponse;
import com.project.societyManagement.dto.User.UserDetails;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.service.AuthService;
import com.project.societyManagement.service.UserService;
import com.project.societyManagement.util.AuthUtil;
import com.project.societyManagement.util.ValidationUtil;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public  class AuthServiceImpl implements AuthService {


    private final AuthUtil authUtil;
    @Autowired
    private  AuthenticationManager authenticationManager;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final ValidationUtil validationUtil;

    public AuthTokenResponse login(LoginRequest loginRequest){

        validationUtil.validate(loginRequest);
        log.info("Authenticating User Credentials...");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword())
        );
        log.info("User Credentials Authenticated...");
        User user = (User) authentication.getPrincipal();
        log.info("Generating Auth Token...");
        String token = authUtil.getAccessToken(user);
        log.info("Auth Token Generated...");
        UserDetails userDetails = modelMapper.map(user,UserDetails.class);
        return new AuthTokenResponse(token,userDetails);
    }

    public AuthTokenResponse register(RegisterRequest registerRequest){
        validationUtil.validate(registerRequest);
        User existingUser = userService.findUserByEmail(registerRequest.getEmail());
        if(existingUser != null){
            log.error("{} already present in Database",registerRequest.getEmail());
            throw new IllegalArgumentException("User with this email already exists.");
        }
        User registerUser = modelMapper.map(registerRequest , User.class);
        log.info("Encoding User's Password ");
        registerUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        try{
            log.info("Saving user in the DB....");
            User user = userService.saveUser(registerUser);
            log.info("User Saved in the DB");
            log.info("Generating Auth Token...");
            String token = authUtil.getAccessToken(user);
            log.info("Auth Token Generated...");
            UserDetails userDetails = modelMapper.map(user,UserDetails.class);
            return new AuthTokenResponse(token,userDetails);
        } catch (Exception e) {
            log.error("Error while registering user...");
            throw new RuntimeException(e.getMessage());
        }
    }


}
