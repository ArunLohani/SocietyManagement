package com.project.societyManagement.service;


import com.project.societyManagement.entity.Tenant;

import java.util.List;

public interface TenantService {

    public Tenant findTenantById(Long id);
    public List<Tenant> getAllTenants();
    public Tenant createTenant(String tenant);
    public Tenant addUserToTenant(Long tenantId , Long userId);
    public Tenant removeUserFromTenant(Long tenantId , Long userId);
}
