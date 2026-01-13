package com.project.societyManagement.dto.FlatMember;

import com.project.societyManagement.entity.types.FlatMembershipType;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlatMemberAddRequest {
    @NotEmpty(message = "Flat cannot be empty.")
    private Long flatId;
    @NotEmpty(message = "User cannot be empty.")
    private Long userId;
    @NotEmpty(message = "FlatMembership Type cannot be empty.")
    private FlatMembershipType type;
}
