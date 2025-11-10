package com.project.societyManagement.service;

import com.project.societyManagement.entity.Menu;
import com.project.societyManagement.entity.TenantRoleMenu;
import com.project.societyManagement.entity.TenantRoleMenuAction;
import com.project.societyManagement.entity.TenantRoles;
import com.project.societyManagement.queryBuilder.tenantRole.TenantRoleFilter;
import com.project.societyManagement.queryBuilder.tenantRoleMenu.TenantRoleMenuFilter;

import java.util.List;

public interface TenantRoleMenuService {
    public TenantRoleMenu assignMenuToTenantRole(Long tenantRoleId, Long menuId);
    public void removeMenuFromTenantRole(Long tenantRoleId, Long menuId);
    public List<TenantRoleMenu> getMenusForTenantRole(Long tenantRoleId);
    public List<TenantRoleMenu> getTenantRolesForMenu(Long menuId);
    public TenantRoleMenu findById(Long id);
    public boolean hasMenuAccess(Long tenantRoleId, Long menuId);
    public TenantRoleMenu searchByTenantRoleAndMenu(Long tenantRoleId, Long menuId);
}
