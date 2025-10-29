package com.project.societyManagement.queryBuilder.tenantRoleMenuAction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantRoleMenuActionFilter {
    private Long id;
    private Long tenantRoleMenuId;
    private Long actionId;
    private boolean isActive;
}
