package com.project.societyManagement.service.impl;

import com.project.societyManagement.entity.User;
import com.project.societyManagement.repository.UserRepo;
import com.project.societyManagement.service.UserService;
import com.project.societyManagement.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ValidationUtil validationUtil;

    public User saveUser(User user){
        validationUtil.validate(user);
        User savedUser = userRepo.save(user);
        return savedUser;
    }

    @Override
    public User findUserByEmail(String email) {
        validationUtil.validate(email);
        return userRepo.findByEmail(email);
    }


}
