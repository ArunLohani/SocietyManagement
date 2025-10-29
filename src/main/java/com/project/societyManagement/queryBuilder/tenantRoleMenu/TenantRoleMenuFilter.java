package com.project.societyManagement.queryBuilder.tenantRoleMenu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantRoleMenuFilter {

    private Long tenantRoleId;
    private Long menuId;
    private Long id;
    private boolean isActive;
}
