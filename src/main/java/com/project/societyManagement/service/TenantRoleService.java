package com.project.societyManagement.service;

import com.project.societyManagement.entity.Role;
import com.project.societyManagement.entity.Tenant;
import com.project.societyManagement.entity.TenantRoles;
import com.project.societyManagement.queryBuilder.tenantRole.TenantRoleFilter;

import java.util.List;

public interface TenantRoleService {
    public List<TenantRoles> getRolesForTenant(Long tenantId);
    public List<TenantRoles> getTenantForRoles(Long roleId);
    public TenantRoles findById(Long id);
    public void assignRoleToTenant(Long tenantId , Long roleId);
    public void removeRoleFromTenant(Long tenantId , Long roleId);
    public TenantRoles searchByTenantAndRole(Long tenantId , Long roleId);
}
