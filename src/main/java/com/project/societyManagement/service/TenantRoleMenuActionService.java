package com.project.societyManagement.service;

import com.project.societyManagement.entity.TenantRoleMenuAction;
import java.util.List;

public interface TenantRoleMenuActionService {
    public TenantRoleMenuAction assignActionToTenantRoleMenu(Long tenantRoleMenuId, Long actionId);
    public void removeActionFromTenantRoleMenu(Long tenantRoleMenuId, Long actionId);
    public List<TenantRoleMenuAction> getActionsForTenantRoleMenu(Long tenantRoleMenuId);
    public List<TenantRoleMenuAction> getTenantRolesMenuForAction(Long actionId);
    public TenantRoleMenuAction findById(Long id);
    public boolean hasActionAccess(Long tenantRoleMenuId, Long actionId);
    public TenantRoleMenuAction searchByTenantRoleMenuAndAction(Long tenantRoleMenuId, Long actionId);
}
