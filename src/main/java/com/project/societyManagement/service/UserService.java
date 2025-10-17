package com.project.societyManagement.service;

import com.project.societyManagement.dto.User.UserDetails;
import com.project.societyManagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    public User saveUser(User user);
    public User findUserByEmail(String email);
    public UserDetails findUserById(Long id);
    public Page<User> searchUser(String name, String email, Pageable pageable);
    public Boolean findExistingUserByEmail(String email);
    public UserDetails updateUser(User user);
}
