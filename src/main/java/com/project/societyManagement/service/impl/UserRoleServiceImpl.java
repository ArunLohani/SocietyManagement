package com.project.societyManagement.service.impl;

import com.project.societyManagement.dto.User.UserWithRolesDTO;
import com.project.societyManagement.entity.Role;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.queryBuilder.user.UserFilter;
import com.project.societyManagement.queryBuilder.user.UserQueryBuilder;
import com.project.societyManagement.repository.RoleRepo;
import com.project.societyManagement.repository.UserRepo;
import com.project.societyManagement.service.RoleService;
import com.project.societyManagement.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private RoleService roleService;
    @Autowired
    private UserQueryBuilder userQueryBuilder;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RoleRepo roleRepo;

    @Override
    @Transactional(readOnly = true)
    public List<UserWithRolesDTO> getAllUsersWithRolesByTenant(Long tenantId) {
        UserFilter userFilter = new UserFilter();
        userFilter.setTenantId(tenantId);
        List<User> users = userQueryBuilder.search(userFilter);
        return users.stream().map(user -> {
            List<Long> roleIds = user.getRoles().stream()
                    .map(Role::getId)
                    .collect(Collectors.toList());
            List<String> roleNames = user.getRoles().stream()
                    .map(Role::getRole)
                    .collect(Collectors.toList());
            return UserWithRolesDTO.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .assignedRoleIds(roleIds)
                    .assignedRoleNames(roleNames)
                    .build();
        }).collect(Collectors.toList());
    }
    @Override
    @Transactional
    public User assignRoleToUser(Long userId, Long roleId) {
        UserFilter userFilter = new UserFilter();
        userFilter.setUserId(userId);
        User user = userQueryBuilder.findById(userFilter);

        Role role = roleService.findById(roleId);

        // Add role if not already present
        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            return userRepo.save(user);
        }
        return user;
    }
    @Override
    @Transactional
    public User removeRoleFromUser(Long userId, Long roleId) {
        UserFilter userFilter = new UserFilter();
        userFilter.setUserId(userId);
        User user = userQueryBuilder.findById(userFilter);

        Role role = roleService.findById(roleId);
        // Remove role if present
        user.getRoles().remove(role);
        return userRepo.save(user);
    }
    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByTenantId(Long tenantId) {
        UserFilter userFilter = new UserFilter();
        userFilter.setTenantId(tenantId);
     return userQueryBuilder.search(userFilter);
    }

    @Override
    public Page<UserWithRolesDTO> getUsersByTenantIdPaginated(Long tenantId, Pageable pageable) {
        UserFilter userFilter = new UserFilter();
        userFilter.setTenantId(tenantId);

        // Get the paginated result of users
        Page<User> userPage = userQueryBuilder.searchPaginated(userFilter, pageable);

        // Convert the Page<User> to Page<UserWithRolesDTO>
        Page<UserWithRolesDTO> userWithRolesDTOPage = userPage.map(user -> {
            List<Long> roleIds = user.getRoles().stream()
                    .map(Role::getId)
                    .collect(Collectors.toList());
            List<String> roleNames = user.getRoles().stream()
                    .map(Role::getRole)
                    .collect(Collectors.toList());

            return UserWithRolesDTO.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .assignedRoleIds(roleIds)
                    .assignedRoleNames(roleNames)
                    .build();
        });

        return userWithRolesDTOPage;
    }

}