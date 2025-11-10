package com.project.societyManagement.service;

import com.project.societyManagement.dto.User.UserWithRolesDTO;
import com.project.societyManagement.entity.User;
import java.util.List;

public interface UserRoleService {
    List<UserWithRolesDTO> getAllUsersWithRolesByTenant(Long tenantId);
    User assignRoleToUser(Long userId, Long roleId);
    User removeRoleFromUser(Long userId, Long roleId);
    List<User> getUsersByTenantId(Long tenantId);
}