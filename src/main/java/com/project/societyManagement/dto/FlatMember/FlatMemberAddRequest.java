package com.project.societyManagement.dto.FlatMember;

import com.project.societyManagement.entity.types.FlatMembershipType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlatMemberAddRequest {
    private Long flatId;
    private Long userId;
    private FlatMembershipType type;
}
