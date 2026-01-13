package com.project.societyManagement.service;

import com.project.societyManagement.entity.TenantRoleMenu;
import java.util.List;

public interface TenantRoleMenuService {
    public TenantRoleMenu assignMenuToTenantRole(Long tenantRoleId, Long menuId);
    public void removeMenuFromTenantRole(Long tenantRoleId, Long menuId);
    public List<TenantRoleMenu> getMenusForTenantRole(Long tenantRoleId);
    public List<TenantRoleMenu> getTenantRolesForMenu(Long menuId);
    public TenantRoleMenu findById(Long id);
    public boolean hasMenuAccess(Long tenantRoleId, Long menuId);
    public TenantRoleMenu searchByTenantRoleAndMenu(Long tenantRoleId, Long menuId);
    public boolean canAccess(String menu);
    public boolean canAccess(String menuName,Long userId);
    public int getUserPriorityOnMenu(String menuName);
}
