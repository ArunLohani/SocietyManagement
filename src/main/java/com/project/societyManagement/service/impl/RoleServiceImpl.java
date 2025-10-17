package com.project.societyManagement.service.impl;

import com.project.societyManagement.entity.Role;
import com.project.societyManagement.queryBuilder.role.RoleFilter;
import com.project.societyManagement.queryBuilder.role.RoleQueryBuilder;
import com.project.societyManagement.repository.RoleRepo;
import com.project.societyManagement.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepo roleRepo;
    private final RoleQueryBuilder roleQueryBuilder;

    @Override
    public Role findByRole(String role) {
        RoleFilter roleFilter = new RoleFilter();
        roleFilter.setRole(role);
        Role roles = roleQueryBuilder.search(roleFilter).get(0);
        return roles;
    }

    @Override
    public Role findById(Long id) {
        RoleFilter roleFilter = new RoleFilter();
        roleFilter.setId(id);
        Role role = roleQueryBuilder.findById(roleFilter);
        return role;
    }

    @Override
    public Role createRole(String roleName){
        Role role = new Role();
        role.setRole(roleName);
         role = roleRepo.save(role);
         return role;
    }
    @Override
    public Role deleteRole(Long id){
        RoleFilter roleFilter = new RoleFilter();
        roleFilter.setId(id);
        Role role = roleQueryBuilder.findById(roleFilter);
        role.setActive(false);
        role = roleRepo.save(role);
        return role;
    }

}
