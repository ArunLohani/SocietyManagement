package com.project.societyManagement.service;


import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.type.Role;
import com.project.societyManagement.repository.UserRepo;
import com.project.societyManagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepo userRepo;

    private User user1;

    @BeforeEach
    void init(){
        user1 = User.builder()
                .name("TestUser1")
                .email("testuser1@gmail.com")
                .password("testPassword")
                .phoneNumber("8171497573")
                .role(Role.TENANT)
                .createdAt(LocalDateTime.now())
                .createdBy(1L)
                .build();
    }

    @Test
    void saveUserTest(){
        when(userRepo.save(any(User.class))).thenReturn(user1);
        User user = userService.saveUser(user1);
        assertNotNull(user);
        assertThat("testuser1@gmail.com").isEqualTo(user.getEmail());
    }

    @Test
    void findUserByEmailTest(){

        when(userRepo.findByEmail(any(String.class))).thenReturn(user1);

        User user=userService.findUserByEmail("testUser1@gmail.com");

        assertNotNull(user);




    }


}
