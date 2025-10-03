package com.project.societyManagement.dto.Auth.Request;


import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotEmpty(message = "Name cannot be blank.")
    @Size(min=6,message = "Name must be of atleast 6 characters long.")
    private String name;

    @NotEmpty
    @Email(message = "Email should be valid.")
    private String email;

    @NotEmpty(message = "Password cannot be blank.")
    @Size(min=8,message = "Password must be of atleast 8 characters long.")
    private String password;

    @Column(name = "phone_number")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;
}

