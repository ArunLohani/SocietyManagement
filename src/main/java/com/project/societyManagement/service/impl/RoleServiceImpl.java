package com.project.societyManagement.service.impl;

import com.project.societyManagement.entity.Role;
import com.project.societyManagement.repository.RoleRepo;
import com.project.societyManagement.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepo roleRepo;

    @Override
    public Role findByRole(String userRole) {
        return roleRepo.findByRole(userRole).orElseThrow(()->
                new RuntimeException("Role not found"));
    }
}
