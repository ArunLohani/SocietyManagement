package com.project.societyManagement.service.impl;

import com.project.societyManagement.dto.User.UserDetails;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.exception.UserNotFoundException;
import com.project.societyManagement.queryBuilder.user.UserFilter;
import com.project.societyManagement.queryBuilder.user.UserQueryBuilder;
import com.project.societyManagement.repository.UserRepo;
import com.project.societyManagement.service.UserService;
import com.project.societyManagement.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ValidationUtil validationUtil;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserQueryBuilder userQueryBuilder;

    public User saveUser(User user){
        validationUtil.validate(user);
        User savedUser = userRepo.save(user);
        return savedUser;
    }



    @Override
    public User findUserByEmail(String email) {
        UserFilter userFilter = new UserFilter();
        userFilter.setEmail(email);
        List<User> users = userQueryBuilder.search(userFilter);
        if(users.isEmpty()){
            throw new UserNotFoundException("User not found with email " + email);}
        User user = users.get(0);
        return user;
    }

    public Boolean findExistingUserByEmail(String email){
        UserFilter userFilter = new UserFilter();
        userFilter.setEmail(email);
        List<User> users = userQueryBuilder.search(userFilter);
        if(users.isEmpty()){
            return false;
        }
        return true;
    }

    @Override
    public UserDetails updateUser(User user) {
        validationUtil.validate(user);
        User updatedUser = userRepo.save(user);
        UserDetails userDetails = UserDetails.builder().id(user.getId()).email(user.getEmail()).name(user.getName()).roles(user.getRoles().stream().map(role -> role.getRole()).collect(Collectors.toSet())).build();
        return userDetails;
    }

    @Override
    public UserDetails findUserById(Long id) {
        UserFilter userFilter = new UserFilter();
        userFilter.setUserId(id);
        User user = userQueryBuilder.findById(userFilter);
//        UserDetails userDetails = modelMapper.map(user,UserDetails.class);
        UserDetails userDetails = UserDetails.builder().id(user.getId()).email(user.getEmail()).name(user.getName()).roles(user.getRoles().stream().map(role -> role.getRole()).collect(Collectors.toSet())).build();
        return userDetails;
    }

    public Page<User> searchUser(String name, String email, Pageable pageable){
        UserFilter userFilter = new UserFilter();
        userFilter.setEmail(email);
        userFilter.setName(name);
        return userQueryBuilder.searchPaginated(userFilter,pageable);
    }


}
