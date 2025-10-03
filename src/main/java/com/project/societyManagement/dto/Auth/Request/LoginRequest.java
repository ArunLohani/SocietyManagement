package com.project.societyManagement.dto.Auth.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotEmpty
    @Email(message = "Email should be valid.")
    private String email;

    @NotEmpty(message = "Password cannot be blank.")
    @Size(min=8,message = "Password must be of atleast 8 characters long.")
    private String password;
}
