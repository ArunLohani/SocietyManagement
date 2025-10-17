package com.project.societyManagement.service.impl;

import com.project.societyManagement.entity.Tenant;
import com.project.societyManagement.repository.TenantRepo;
import com.project.societyManagement.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantRepo tenantRepo;

    @Override
    public Tenant findTenantById(Long id) {
        return tenantRepo.findById(id).orElse(null);
    }
}
