package com.project.societyManagement.service.impl;

import com.project.societyManagement.entity.Role;
import com.project.societyManagement.entity.Tenant;
import com.project.societyManagement.entity.TenantRoles;
import com.project.societyManagement.queryBuilder.tenantRole.TenantRoleFilter;
import com.project.societyManagement.queryBuilder.tenantRole.TenantRoleQueryBuilder;
import com.project.societyManagement.repository.TenantRoleRepo;
import com.project.societyManagement.service.RoleService;
import com.project.societyManagement.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenantRoleServiceImpl {

    @Autowired
    private TenantRoleQueryBuilder tenantRoleQueryBuilder;
    @Autowired
    private TenantRoleRepo tenantRoleRepo;
    @Autowired
    private RoleService roleService;
    @Autowired
    private TenantService tenantService;

    public void assignRoleToTenant(Long tenantId , Long roleId){
        Role role = roleService.findById(roleId);
        Tenant tenant = tenantService.findTenantById(tenantId);
        TenantRoleFilter tenantRoleFilter = new TenantRoleFilter();
        tenantRoleFilter.setTenantId(tenantId);
        tenantRoleFilter.setRoleId(roleId);
        TenantRoles tenantRoles = tenantRoleQueryBuilder.findById(tenantRoleFilter);
        if(tenantRoles == null){
            tenantRoles = new TenantRoles();
            tenantRoles.setTenant(tenant);
            tenantRoles.setRole(role);
            tenantRoleRepo.save(tenantRoles);
        }
    }

    public void removeRoleFromTenant(Long tenantId , Long roleId){
        Role role = roleService.findById(roleId);
        Tenant tenant = tenantService.findTenantById(tenantId);
        TenantRoleFilter tenantRoleFilter = new TenantRoleFilter();
        tenantRoleFilter.setTenantId(tenantId);
        tenantRoleFilter.setRoleId(roleId);
        TenantRoles tenantRoles = tenantRoleQueryBuilder.findById(tenantRoleFilter);
        if(tenantRoles != null){
            tenantRoles.setActive(false);
            tenantRoleRepo.save(tenantRoles);
        }
    }
}
