package com.project.societyManagement.service;

import com.project.societyManagement.dto.Auth.Request.LoginRequest;
import com.project.societyManagement.dto.Auth.Request.RegisterRequest;
import com.project.societyManagement.dto.Auth.Response.AuthTokenResponse;
import com.project.societyManagement.dto.User.UserDetails;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.service.impl.AuthServiceImpl;
import com.project.societyManagement.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserService userService;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    private User user1;

    @BeforeEach
    void init(){
        user1 = User.builder()
                .id(1L)
                .name("TestUser1")
                .email("testuser1@gmail.com")
                .password("testPassword")
                .phoneNumber("8171497573")
//                .role(Role.TENANT)
                .createdAt(LocalDateTime.now())
                .createdBy(1L)
                .build();
    }

    @Test
    void loginTest(){
        LoginRequest loginRequest = new LoginRequest("testuser1@gmail.com","testPassword");
        UserDetails userDetails = new UserDetails(1L,"TestUser1","testuser1@gmail,com", "TENANT");
        AuthTokenResponse mockAuthTokenResponse = new AuthTokenResponse("MOCK_TOKEN",userDetails);
        Authentication mockAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(user1);
        when(authUtil.getAccessToken(user1)).thenReturn("MOCK_TOKEN");
        when(modelMapper.map(user1,UserDetails.class)).thenReturn(userDetails);
        AuthTokenResponse actualResponse = authService.login(loginRequest);
        assertNotNull(actualResponse);
        assertEquals(actualResponse.getToken(),mockAuthTokenResponse.getToken());
        assertEquals(actualResponse.getUser().getEmail(),mockAuthTokenResponse.getUser().getEmail());
    }

    @Test
    void registerTestSuccess(){
        RegisterRequest registerRequest = new RegisterRequest("TestUser1", "testuser1@gmail.com"
                ,"testPassword","8171497573");
        UserDetails userDetails = new UserDetails(1L,"TestUser1","testuser1@gmail,com", "TENANT");
        AuthTokenResponse mockAuthTokenResponse = new AuthTokenResponse("MOCK_TOKEN",userDetails);
        when(userService.findUserByEmail(registerRequest.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedNewPassword");
        when(modelMapper.map(registerRequest,User.class)).thenReturn(user1);
        when(userService.saveUser(any(User.class))).thenReturn(user1);
        when(modelMapper.map(user1,UserDetails.class)).thenReturn(userDetails);
        when(authUtil.getAccessToken(user1)).thenReturn("MOCK_TOKEN");
        AuthTokenResponse actualResponse = authService.register(registerRequest);
        assertNotNull(actualResponse);
        assertEquals(actualResponse.getToken(),mockAuthTokenResponse.getToken());
        assertEquals(actualResponse.getUser().getEmail(),mockAuthTokenResponse.getUser().getEmail());
    }

    @Test
    void registerTestFailure(){
        RegisterRequest registerRequest = new RegisterRequest("TestUser1", "testuser1@gmail.com"
                ,"testPassword","8171497573");
        when(userService.findUserByEmail(registerRequest.getEmail())).thenReturn(user1);
        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
    }

}
