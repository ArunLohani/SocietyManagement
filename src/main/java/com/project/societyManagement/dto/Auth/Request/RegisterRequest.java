package com.project.societyManagement.dto.Auth.Request;


import com.project.societyManagement.entity.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @NotEmpty(message = "Name cannot be blank.")
    @Size(min=6,message = "Name must be of atleast 6 characters long.")
    private String name;

    public RegisterRequest(String name, String email, String password, String phoneNumber, Set<String> roles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
        this.roles.stream().map(role -> role.toUpperCase()).collect(Collectors.toSet());
    }

    @NotEmpty
    @Email(message = "Email should be valid.")
    private String email;

    @NotEmpty(message = "Password cannot be blank.")
    @Size(min=8,message = "Password must be of atleast 8 characters long.")
    private String password;

    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;

    private Set<String> roles;
}

