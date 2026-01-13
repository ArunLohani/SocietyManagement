package com.project.societyManagement.dto.Role;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleAssignmentRequest {
    @NotEmpty(message = "User cannot be null.")
    private Long userId;
    @NotEmpty(message="Role cannot be null.")
    private Long roleId;
}