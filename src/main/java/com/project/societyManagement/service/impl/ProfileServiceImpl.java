package com.project.societyManagement.service.impl;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.User.UserDetails;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.service.ProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Override
    public ApiResponse<UserDetails> getMyProfile(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        UserDetails userDetails = UserDetails.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .roles(user.getRoles().stream().map(role ->
                        role.getRole()).collect(Collectors.toSet())).build();
        return new ApiResponse<>(true,"Your Profile has been fetched successfully",userDetails);



    }
}
