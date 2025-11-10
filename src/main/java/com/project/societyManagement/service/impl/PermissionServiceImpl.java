package com.project.societyManagement.service.impl;

import com.project.societyManagement.entity.*;
import com.project.societyManagement.entity.types.Actions;
import com.project.societyManagement.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final TenantRoleMenuService tenantRoleMenuService;
    private final TenantRoleMenuActionService tenantRoleMenuActionService;
    private final MenuService menuService;
    private final ActionService actionService;
    private final TenantRoleService tenantRoleService;

    public boolean hasPermission(User user, String menuName, Actions actionName) {
        if (user == null || menuName == null || actionName == null) {
            return false;
        }

        Menu menu = menuService.findMenuByName(menuName);
        Action action = actionService.findByAction(actionName.name());

        if (menu == null || action == null) {
            return false;
        }

        Tenant tenant = user.getTenant();
        if (tenant == null) {
            return false;
        }

        Set<Role> roles = user.getRoles();
        if (roles == null || roles.isEmpty()) {
            return false;
        }

        // Fetch all TenantRoles for this user and tenant
        List<TenantRoles> tenantRoles = roles.stream()
                .map(role -> tenantRoleService.searchByTenantAndRole(role.getId(), tenant.getId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (tenantRoles.isEmpty()) {
            return false;
        }

        // Check for menu access
        List<TenantRoleMenu> accessibleMenus = tenantRoles.stream()
                .map(tenantRole -> tenantRoleMenuService.searchByTenantRoleAndMenu(tenantRole.getId(), menu.getId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (accessibleMenus.isEmpty()) {
            return false;
        }

        // Check for action access
        boolean hasActionPermission = accessibleMenus.stream()
                .anyMatch(tenantRoleMenu ->
                        tenantRoleMenuActionService.hasActionAccess(tenantRoleMenu.getId(), action.getId())
                );

        return hasActionPermission;
    }
}
