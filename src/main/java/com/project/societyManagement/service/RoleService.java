package com.project.societyManagement.service;

import com.project.societyManagement.entity.Role;

import java.util.List;

public interface RoleService {

    public Role findByRole(String role);
    public Role findById(Long id);
    public Role createRole(String roleName);
    public Role deleteRole(Long id);
    public List<Role> searchRole();
}
