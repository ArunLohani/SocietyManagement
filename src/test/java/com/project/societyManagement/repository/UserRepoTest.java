package com.project.societyManagement.repository;


import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.type.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserRepoTest {


    @Autowired
    private UserRepo repo;

    private User user1;
    private User user2;


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

        user2 = User.builder()
                .name("TestUser2")
                .email("testuser2@gmail.com")
                .password("testPassword")
                .phoneNumber("8171497573")
                .role(Role.TENANT)
                .createdAt(LocalDateTime.now())
                .createdBy(1L)
                .build();




    }


    @Test
    @DisplayName("User should be saved in the DB")
    void createUserTest()
    {
        User user = repo.save(user1);

        assertNotNull(user);
        assertEquals("testuser1@gmail.com",user.getEmail());



    }

    @Test
    void getAllUsers(){

        User u1 = repo.save(user1);
        User u2 = repo.save(user2);



        List<User> users = repo.findAll();
        assertNotNull(users);
        assertThat(3).isEqualTo(users.size());

    }

    @Test
    void getUserById(){

      User savedUser =   repo.save(user1);
      Long id = savedUser.getId();
        User user = repo.findById(id).get();

        assertNotNull(user);
       assertEquals(id,user.getId());


    }

    @Test
    void getUserByEmail(){

        User savedUser = repo.save(user1);

        User user = repo.findByEmail(savedUser.getEmail());

        assertNotNull(user);

        assertThat(user.getId()).isEqualTo(savedUser.getId());



    }

    @Test
    void deleteUser(){

        User savedUser = repo.save(user1);

        repo.deleteById(savedUser.getId());


        User deletedUser = repo.findById(savedUser.getId()).orElse(null);

        assertNull(deletedUser);




    }




}
