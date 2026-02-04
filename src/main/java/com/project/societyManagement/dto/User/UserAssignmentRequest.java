package com.project.societyManagement.dto.User;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAssignmentRequest {

    @NotNull(message = "Tenant cannot be null.")
    private Long tenantId;

    @NotNull(message = "User cannot be null.")
    private Long userId;
}
