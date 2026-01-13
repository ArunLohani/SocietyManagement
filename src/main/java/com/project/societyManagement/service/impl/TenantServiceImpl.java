package com.project.societyManagement.service.impl;

import com.project.societyManagement.entity.*;
import com.project.societyManagement.queryBuilder.tenant.TenantFilter;
import com.project.societyManagement.queryBuilder.tenant.TenantQueryBuilder;
import com.project.societyManagement.repository.TenantRepo;
import com.project.societyManagement.service.TenantService;
import com.project.societyManagement.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantRepo tenantRepo;
    @Autowired
    private TenantQueryBuilder tenantQueryBuilder;
    @Autowired
    private UserService userService;

    @Override
    public Tenant findTenantById(Long id) {

        TenantFilter tenantFilter = new TenantFilter();
        tenantFilter.setId(id);

        return tenantQueryBuilder.findById(tenantFilter);
    }

    @Override
    public List<Tenant> getAllTenants(){

        TenantFilter tenantFilter = new TenantFilter();
        return tenantQueryBuilder.search(tenantFilter);
    }

    @Override
    public Tenant createTenant(String  tenant) {
        Tenant tenant1 = new Tenant();
        tenant1.setName(tenant);
       return tenantRepo.save(tenant1);
    }

    @Override
    public Tenant addUserToTenant(Long tenantId , Long userId){
        // Fetch tenant
        TenantFilter tenantFilter = new TenantFilter();
        tenantFilter.setId(tenantId);
        Tenant tenant = tenantQueryBuilder.findById(tenantFilter);
        // Fetch User
        User user = userService.findUserById(userId);
        user.setTenant(tenant);
        List<User> residents = tenant.getResidents();
        residents.add(user);
        tenant.setResidents(residents);
        tenant = tenantRepo.save(tenant);
        return tenant;
    }
    @Override
    @Transactional
    public Tenant removeUserFromTenant(Long tenantId, Long userId) {

        TenantFilter tenantFilter = new TenantFilter();
        tenantFilter.setId(tenantId);
        Tenant tenant = tenantQueryBuilder.findById(tenantFilter);
        User user = userService.findUserById(userId);
        // Retain SUPER_ADMIN only if present
        Set<Role> updatedRoles = user.getRoles().stream()
                .filter(role -> "SUPER_ADMIN".equals(role.getRole()))
                .collect(Collectors.toSet());
        // If SUPER_ADMIN not present → roles become empty
        user.setRoles(updatedRoles);
        // Remove tenant mapping
        user.setTenant(null);
        userService.updateUser(user);
        // Remove user from tenant residents
        tenant.getResidents().removeIf(u -> u.getId().equals(userId));

        return tenant;
    }

    @Override
    @Transactional
    public Tenant removeTenant(Long tenantId){
        TenantFilter tenantFilter = new TenantFilter();
        tenantFilter.setId(tenantId);
        Tenant tenant = tenantQueryBuilder.findById(tenantFilter);
        tenant.setIsActive(false);
        tenant = tenantRepo.save(tenant);
        return tenant;
    }



}
