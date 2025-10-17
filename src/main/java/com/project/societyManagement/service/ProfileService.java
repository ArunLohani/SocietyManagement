package com.project.societyManagement.service;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.User.UserDetails;
import org.springframework.security.core.Authentication;

public interface ProfileService {

    public ApiResponse<UserDetails> getMyProfile(Authentication authentication);

}
