package com.project.societyManagement.service.impl;

import com.project.societyManagement.entity.*;
import com.project.societyManagement.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final TenantRoleMenuService tenantRoleMenuService;
    private final TenantRoleMenuActionService tenantRoleMenuActionService;
    private final MenuService menuService;
    private final ActionService actionService;
    private final TenantRoleService tenantRoleService;
    private final ApiMappingService apiMappingService;

    @Override
    public boolean hasPermission(User user, String api) {
        if (user == null) {
            log.warn("Permission denied: user is null");
            return false;
        }

        Set<Role> roles = user.getRoles();
        if (roles == null || roles.isEmpty()) {
            log.warn("Permission denied: user {} has no roles", user.getUsername());
            return false;
        }

        String menuName;
        try {
            String[] parts = api.split("_", 2);
            menuName = parts[1].replace("_", " ");
        } catch (Exception e) {
            log.error("Invalid API format '{}', expected format ACTION_MENU", api);
            return false;
        }


        log.info("Checking permission for API: {} (Menu: {})", api, menuName);

        Menu menu;
        try {
            menu = menuService.findMenuByName(menuName);
        } catch (Exception e) {
            log.warn("Menu not found for name '{}': {}", menuName, e.getMessage());
            return false;
        }

        if (menu == null) {
            log.warn("No menu found for name '{}'", menuName);
            return false;
        }

        ApiPermissionMapping apiPermissionMapping;
        try {
            apiPermissionMapping = apiMappingService.findApiPermissionMappingByApiName(api);
        } catch (Exception e) {
            log.warn("API permission mapping not found for '{}': {}", api, e.getMessage());
            return false;
        }

        if (apiPermissionMapping == null || apiPermissionMapping.getAction() == null) {
            log.warn("No action mapping found for API '{}'", api);
            return false;
        }

        Action action = apiPermissionMapping.getAction();
        Integer priority = action.getPriority();

        Tenant tenant = user.getTenant();
        if (tenant == null) {
            log.warn("Permission denied: user {} has no tenant assigned", user.getUsername());
            return false;
        }

        // --- Fetch TenantRoles ---
        List<TenantRoles> tenantRoles = new ArrayList<>();
        for (Role role : roles) {
            try {
                TenantRoles tenantRole = tenantRoleService.searchByTenantAndRole(tenant.getId(), role.getId());
                if (tenantRole != null) {
                    tenantRoles.add(tenantRole);
                }
            } catch (Exception e) {
                log.warn("TenantRole not found for tenant {} and role {}: {}", tenant.getId(), role.getId(), e.getMessage());
            }
        }

        if (tenantRoles.isEmpty()) {
            log.warn("No tenant roles found for user {}", user.getUsername());
            return false;
        }

        // --- Fetch Menus for those roles ---
        List<TenantRoleMenu> accessibleMenus = new ArrayList<>();
        for (TenantRoles tenantRole : tenantRoles) {
            try {
                TenantRoleMenu trm = tenantRoleMenuService.searchByTenantRoleAndMenu(tenantRole.getId(), menu.getId());
                if (trm != null) {
                    accessibleMenus.add(trm);
                }
            } catch (Exception e) {
                log.warn("No menu found for tenant role {} and menu {}: {}", tenantRole.getId(), menu.getId(), e.getMessage());
            }
        }

        if (accessibleMenus.isEmpty()) {
            log.warn("User {} has no access to menu '{}'", user.getUsername(), menuName);
            return false;
        }

        // --- Check Action Permissions ---
        boolean hasActionPermission = accessibleMenus.stream()
                .anyMatch(trm -> {
                    try {
                        return trm.getPriority() >= priority;
                    } catch (Exception e) {
                        log.warn("Error comparing priorities for TRM {}: {}", trm.getId(), e.getMessage());
                        return false;
                    }
                });

        if (!hasActionPermission) {
            log.warn("User {} lacks permission for API '{}'", user.getUsername(), api);
        } else {
            log.info("User {} granted access to '{}'", user.getUsername(), api);
        }

        return hasActionPermission;
    }
}
