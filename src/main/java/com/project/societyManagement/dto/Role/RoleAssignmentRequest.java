package com.project.societyManagement.dto.Role;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleAssignmentRequest {
    @NotNull(message = "User cannot be null.")
    private Long userId;
    @NotNull(message="Role cannot be null.")
    private Long roleId;
}