package com.project.societyManagement.service;

import com.project.societyManagement.entity.Role;

public interface RoleService {

    public Role findByRole(String role);
    public Role findById(Long id);
    public Role createRole(String roleName);
    public Role deleteRole(Long id);
}
