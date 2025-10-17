package com.project.societyManagement.queryBuilder.tenantRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantRoleFilter {
    private Long tenantId;
    private Long roleId;
    private Long id;

}
