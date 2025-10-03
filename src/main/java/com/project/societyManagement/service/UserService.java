package com.project.societyManagement.service;

import com.project.societyManagement.entity.User;

public interface UserService {
    public User saveUser(User user);
    public User findUserByEmail(String email);
}
