package com.project.societyManagement.service.impl;

import com.project.societyManagement.dto.Auth.Request.LoginRequest;
import com.project.societyManagement.dto.Auth.Request.RegisterRequest;
import com.project.societyManagement.dto.Auth.Response.AuthTokenResponse;
import com.project.societyManagement.dto.User.UserDetails;
import com.project.societyManagement.entity.Role;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.service.AuthService;
import com.project.societyManagement.service.RoleService;
import com.project.societyManagement.service.UserService;
import com.project.societyManagement.util.AuthUtil;
import com.project.societyManagement.util.ValidationUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthUtil authUtil;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final ValidationUtil validationUtil;
    private final RoleService roleService;
    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthTokenResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        validationUtil.validate(loginRequest);
        log.info("Authenticating User Credentials...");
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        log.info("User Credentials Authenticated...");
        User user = (User) authentication.getPrincipal();
        log.info("Generating Auth Token...");
        String token = authUtil.getAccessToken(user);
        ResponseCookie jwtCookie = ResponseCookie.from("access_token", token).sameSite("Lax")
                .httpOnly(false).secure(false).path("/").maxAge(Duration.ofDays(7)).build();
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        log.info("Auth Token Generated...");
        UserDetails userDetails = UserDetails.builder().id(user.getId()).email(user.getEmail()).name(user.getName()).roles(user.getRoles().stream().map(role -> role.getRole()).collect(Collectors.toSet())).build();
        return new AuthTokenResponse(token, userDetails);
    }

    public AuthTokenResponse register(RegisterRequest registerRequest, HttpServletResponse response) {
        validationUtil.validate(registerRequest);
        User existingUser = userService.findUserByEmail(registerRequest.getEmail());
        if (existingUser != null) {
            log.error("{} already present in Database", registerRequest.getEmail());
            throw new IllegalArgumentException("User with this email already exists.");
        }
        User registerUser = modelMapper.map(registerRequest, User.class);
        Set<String> roleSet = registerRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleSet) {
            Role r = roleService.findByRole(roleName);
            roles.add(r);
        }
        registerUser.setRoles(roles);
        registerUser.setRoles(roles);
        log.info("Encoding User's Password ");
        registerUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        try {
            log.info("Saving user in the DB....");
            User user = userService.saveUser(registerUser);
            log.info("User Saved in the DB");
            log.info("Generating Auth Token...");
            String token = authUtil.getAccessToken(user);
            ResponseCookie jwtCookie = ResponseCookie.from("access_token", token).sameSite("Lax")
                    .httpOnly(false).secure(false).path("/").maxAge(Duration.ofDays(7)).build();
            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
            log.info("Auth Token Generated...");
            UserDetails userDetails = UserDetails.builder().id(user.getId()).email(user.getEmail()).name(user.getName()).roles(user.getRoles().stream().map(role -> role.getRole()).collect(Collectors.toSet())).build();
            return new AuthTokenResponse(token, userDetails);
        } catch (Exception e) {
            log.error("Error while registering user...");
            throw new RuntimeException(e.getMessage());
        }
    }


}
