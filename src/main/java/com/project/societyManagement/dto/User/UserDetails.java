package com.project.societyManagement.dto.User;

import com.project.societyManagement.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetails {
    private Long id;
    private String name;
    private String email;
    private Set<String> roles;
}
