package com.project.societyManagement.service;

import com.project.societyManagement.dto.User.UserDetails;
import com.project.societyManagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    public User saveUser(User user);
    public User findUserByEmail(String email);
    public User findUserById(Long id);
    public Page<User> searchUser(String name, String email, Pageable pageable);
    public List<User> searchUserList(String name, String email);
    public Boolean findExistingUserByEmail(String email);
    public UserDetails updateUser(User user);
    public User findUserByEmailWithoutAuth(String email);
    public List<UserDetails> findUsersNotAssignedToTenant();
    public Boolean checkUserTenanStatus(Authentication authentication);
}
