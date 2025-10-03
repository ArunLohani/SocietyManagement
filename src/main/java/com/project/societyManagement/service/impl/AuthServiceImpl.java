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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public  class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ValidationUtil validationUtil;

    public AuthTokenResponse login(LoginRequest loginRequest){
        validationUtil.validate(loginRequest);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        String token = authUtil.getAccessToken(user);
        UserDetails userDetails = modelMapper.map(user,UserDetails.class);
        return new AuthTokenResponse(token,userDetails);
    }

    public AuthTokenResponse register(RegisterRequest registerRequest){
        validationUtil.validate(registerRequest);
        User existingUser = userService.findUserByEmail(registerRequest.getEmail());
        if(existingUser != null){
            throw new IllegalArgumentException("User with this email already exists.");
        }
        User registerUser = modelMapper.map(registerRequest , User.class);

        registerUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        try{
            User user = userService.saveUser(registerUser);
            String token = authUtil.getAccessToken(user);
            UserDetails userDetails = modelMapper.map(user,UserDetails.class);
            return new AuthTokenResponse(token,userDetails);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
