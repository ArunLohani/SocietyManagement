package com.project.societyManagement.dto.User;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAssignmentRequest {
    @NotEmpty(message = "Tenant cannot be empty.")
    private Long tenantId;
    @NotEmpty(message = "User cannot be empty")
    private Long userId;
}
