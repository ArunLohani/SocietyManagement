package com.project.societyManagement.controller;


import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.User.UserDetails;
import com.project.societyManagement.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ProfileController {

    @Autowired
    private ProfileService profileService;


    @GetMapping("/profile/me")
    public ResponseEntity<ApiResponse<UserDetails>> getMyProfile(Authentication authentication){
        return ResponseEntity.ok(profileService.getMyProfile(authentication));
    }
}
