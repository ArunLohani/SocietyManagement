package com.project.societyManagement.service;

import com.project.societyManagement.dto.User.UserWithRolesDTO;
import com.project.societyManagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRoleService {
    List<UserWithRolesDTO> getAllUsersWithRolesByTenant(Long tenantId);
    User assignRoleToUser(Long userId, Long roleId);
    User removeRoleFromUser(Long userId, Long roleId);
    List<User> getUsersByTenantId(Long tenantId);
    Page<UserWithRolesDTO> getUsersByTenantIdPaginated(Long tenantId , Pageable pageable);
}