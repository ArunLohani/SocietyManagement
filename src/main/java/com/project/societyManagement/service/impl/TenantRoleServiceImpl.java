package com.project.societyManagement.service.impl;

import com.project.societyManagement.entity.Role;
import com.project.societyManagement.entity.Tenant;
import com.project.societyManagement.entity.TenantRoleMenu;
import com.project.societyManagement.entity.TenantRoles;
import com.project.societyManagement.queryBuilder.tenantRole.TenantRoleFilter;
import com.project.societyManagement.queryBuilder.tenantRole.TenantRoleQueryBuilder;
import com.project.societyManagement.queryBuilder.tenantRoleMenu.TenantRoleMenuFilter;
import com.project.societyManagement.repository.TenantRoleRepo;
import com.project.societyManagement.service.RoleService;
import com.project.societyManagement.service.TenantRoleService;
import com.project.societyManagement.service.TenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class TenantRoleServiceImpl implements TenantRoleService {

    @Autowired
    private TenantRoleQueryBuilder tenantRoleQueryBuilder;
    @Autowired
    private TenantRoleRepo tenantRoleRepo;
    @Autowired
    private RoleService roleService;
    @Autowired
    private TenantService tenantService;

    public List<TenantRoles> getRolesForTenant(Long tenantId) {
        log.info("Fetching roles for tenant role {}", tenantId);
        TenantRoleFilter filter = new TenantRoleFilter();
        filter.setTenantId(tenantId);
        return tenantRoleQueryBuilder.search(filter);
    }

    public List<TenantRoles> getTenantForRoles(Long roleId) {
        log.info("Fetching tenant  for roles {}", roleId);
        TenantRoleFilter filter = new TenantRoleFilter();
        filter.setRoleId(roleId);
        return tenantRoleQueryBuilder.search(filter);
    }

    public TenantRoles findById(Long id) {
        log.info("Fetching tenant role  mapping with id {}", id);
        TenantRoleFilter filter = new TenantRoleFilter();
        filter.setId(id);
        return tenantRoleQueryBuilder.findById(filter);
    }

    public void assignRoleToTenant(Long tenantId , Long roleId){
        Role role = roleService.findById(roleId);
        Tenant tenant = tenantService.findTenantById(tenantId);
        TenantRoleFilter tenantRoleFilter = new TenantRoleFilter();
        tenantRoleFilter.setTenantId(tenantId);
        tenantRoleFilter.setRoleId(roleId);
        tenantRoleFilter.setIsActive(false);
        TenantRoles tenantRoles = null;
        try{
             tenantRoles = tenantRoleQueryBuilder.findById(tenantRoleFilter);
             tenantRoles.setActive(true);
             tenantRoleRepo.save(tenantRoles);

        } catch (Exception e) {
            tenantRoles = new TenantRoles();
            tenantRoles.setTenant(tenant);
            tenantRoles.setRole(role);
            tenantRoles.setActive(true);
            System.out.println("tenantRoles " + tenantRoles);
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

    public TenantRoles searchByTenantAndRole(Long tenantId , Long roleId){
        Role role = roleService.findById(roleId);
        Tenant tenant = tenantService.findTenantById(tenantId);
        TenantRoleFilter tenantRoleFilter = new TenantRoleFilter();
        tenantRoleFilter.setTenantId(tenantId);
        tenantRoleFilter.setRoleId(roleId);
        TenantRoles tenantRoles = tenantRoleQueryBuilder.findById(tenantRoleFilter);
        return tenantRoles;
    }
}
