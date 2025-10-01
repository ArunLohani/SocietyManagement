package com.project.societyManagement.dto.Auth.Response;


import com.project.societyManagement.dto.User.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokenResponse {

    private String token;

    private UserDetails user;


}
