package com.project.societyManagement.service.impl;

import com.project.societyManagement.dto.Auth.Request.RegisterRequest;
import com.project.societyManagement.dto.Auth.Response.AuthTokenResponse;
import com.project.societyManagement.dto.User.UserDetails;
import com.project.societyManagement.entity.Role;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.repository.RoleRepo;
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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2ServiceImpl {

    private final UserService userService;
    private final AuthUtil authUtil;
    private final ModelMapper modelMapper;
    private final RoleRepo roleRepo;

    public AuthTokenResponse handleOauth2LoginRequest(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        User user = userService.findUserByEmail(email);
        AuthTokenResponse authTokenResponse = null;
        //Signup
        if (user == null) {
                Set<Role> roles = new HashSet<>();
                roles.add( roleRepo.findByRole("TENANT").orElse(null));
                user = User.builder()
                        .email(email)
                        .name(name)
                        .roles(roles)
                        .password(null)
                        .build();
                user = userService.saveUser(user);
                log.info("User Saved in the DB");

        }

        log.info("Generating Auth Token...");
        String token = authUtil.getAccessToken(user);
        UserDetails userDetails = UserDetails.builder().id(user.getId()).email(user.getEmail()).name(user.getName()).roles(user.getRoles().stream().map(role -> role.getRole()).collect(Collectors.toSet())).build();
            authTokenResponse = new AuthTokenResponse(token,userDetails);


        return authTokenResponse;

    }
}
