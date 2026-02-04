package com.project.societyManagement.service.impl;

import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.entity.*;
import com.project.societyManagement.queryBuilder.tenantRole.TenantRoleFilter;
import com.project.societyManagement.queryBuilder.tenantRole.TenantRoleQueryBuilder;
import com.project.societyManagement.queryBuilder.tenantRoleMenu.TenantRoleMenuFilter;
import com.project.societyManagement.queryBuilder.tenantRoleMenu.TenantRoleMenuQueryBuilder;
import com.project.societyManagement.repository.TenantRoleMenuRepo;
import com.project.societyManagement.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class TenantRoleMenuServiceImpl implements TenantRoleMenuService {
    @Autowired
    private TenantRoleMenuQueryBuilder tenantRoleMenuQueryBuilder;
    @Autowired
    private TenantRoleMenuRepo tenantRoleMenuRepo;
    @Autowired
    private TenantRoleQueryBuilder tenantRoleQueryBuilder;
    @Autowired
    private MenuService menuService;
    @Autowired
    private ActionService actionService;
    @Autowired
    private TenantRoleMenuActionService tenantRoleMenuActionService;
    @Autowired
    private UserService userService;

    @CacheEvict(value = "canAccessMenu", allEntries = true)
    public TenantRoleMenu assignMenuToTenantRole(Long tenantRoleId, Long menuId) {
        log.info("Assigning menu {} to tenant role {}", menuId, tenantRoleId);

        TenantRoleFilter tenantRoleFilter = new TenantRoleFilter();
        tenantRoleFilter.setId(tenantRoleId);
        TenantRoles tenantRole = tenantRoleQueryBuilder.findById(tenantRoleFilter);

        Menu menu = menuService.findMenuById(menuId);

        TenantRoleMenuFilter filter = new TenantRoleMenuFilter();
        filter.setTenantRoleId(tenantRoleId);
        filter.setMenuId(menuId);
        List<TenantRoleMenu> existing = tenantRoleMenuQueryBuilder.search(filter);

        if (!existing.isEmpty()) {
            TenantRoleMenu existingMapping = existing.get(0);
            if (!existingMapping.getIsActive()) {
                existingMapping.setIsActive(true);
                log.info("🗑️ Cache evicted - Menu permission changed");
                return tenantRoleMenuRepo.save(existingMapping);
            }
            log.info("Menu already assigned to tenant role");
            return existingMapping;
        }

        Action action = actionService.findByAction("READ");

        TenantRoleMenu tenantRoleMenu = TenantRoleMenu.builder()
                .tenantRoles(tenantRole)
                .menu(menu)
                .priority(action.getPriority())
                .isActive(true)
                .build();
        tenantRoleMenu = tenantRoleMenuRepo.save(tenantRoleMenu);

        tenantRoleMenuActionService.assignActionToTenantRoleMenu(
                tenantRoleMenu.getId(), action.getId());

        log.info("🗑️ Cache evicted - New menu permission added");
        return tenantRoleMenu;
    }

    @CacheEvict(value = "canAccessMenu", allEntries = true)
    public void removeMenuFromTenantRole(Long tenantRoleId, Long menuId) {
        log.info("Removing menu {} from tenant role {}", menuId, tenantRoleId);

        TenantRoleMenuFilter filter = new TenantRoleMenuFilter();
        filter.setTenantRoleId(tenantRoleId);
        filter.setMenuId(menuId);
        List<TenantRoleMenu> tenantRoleMenus = tenantRoleMenuQueryBuilder.search(filter);

        if (!tenantRoleMenus.isEmpty()) {
            TenantRoleMenu tenantRoleMenu = tenantRoleMenus.get(0);
            tenantRoleMenu.setIsActive(false);
            tenantRoleMenuRepo.save(tenantRoleMenu);
            log.info("🗑️ Cache evicted - Menu permission removed");
        } else {
            log.warn("No mapping found between tenant role {} and menu {}",
                    tenantRoleId, menuId);
        }
    }

    public List<TenantRoleMenu> getMenusForTenantRole(Long tenantRoleId) {
        log.info("Fetching menus for tenant role {}", tenantRoleId);
        TenantRoleMenuFilter filter = new TenantRoleMenuFilter();
        filter.setTenantRoleId(tenantRoleId);
        return tenantRoleMenuQueryBuilder.search(filter);
    }

    public List<TenantRoleMenu> getTenantRolesForMenu(Long menuId) {
        log.info("Fetching tenant roles for menu {}", menuId);
        TenantRoleMenuFilter filter = new TenantRoleMenuFilter();
        filter.setMenuId(menuId);
        return tenantRoleMenuQueryBuilder.search(filter);
    }

    public TenantRoleMenu findById(Long id) {
        log.info("Fetching tenant role menu mapping with id {}", id);
        TenantRoleMenuFilter filter = new TenantRoleMenuFilter();
        filter.setId(id);
        return tenantRoleMenuQueryBuilder.findById(filter);
    }

    public boolean hasMenuAccess(Long tenantRoleId, Long menuId) {
        TenantRoleMenuFilter filter = new TenantRoleMenuFilter();
        filter.setTenantRoleId(tenantRoleId);
        filter.setMenuId(menuId);
        List<TenantRoleMenu> mappings = tenantRoleMenuQueryBuilder.search(filter);
        return !mappings.isEmpty() && mappings.get(0).getIsActive();
    }

    public TenantRoleMenu searchByTenantRoleAndMenu(Long tenantRoleId, Long menuId) {
        TenantRoleMenuFilter filter = new TenantRoleMenuFilter();
        filter.setTenantRoleId(tenantRoleId);
        filter.setMenuId(menuId);
        return tenantRoleMenuQueryBuilder.findById(filter);
    }

    // ✅ All-in-one: Cache key using SpEL (no separate KeyGenerator needed)
    @Cacheable(
            value = "canAccessMenu",
            key = "T(com.project.societyManagement.config.TenantContextHolder).getCurrentTenant() + ':' + " +
                    "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getPrincipal().id + ':' + " +
                    "#menuName"
    )
    @Override
    public boolean canAccess(String menuName) {
        log.info("🔥 CACHE MISS - Checking menu access for: {}", menuName);

        Menu menu = menuService.findMenuByName(menuName);
        if (menu == null) {
            log.warn("Menu '{}' not found", menuName);
            return false;
        }

        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (user == null) {
            return false;
        }

        Long tenantId = TenantContextHolder.getCurrentTenant();
        if (tenantId == null) {
            log.warn("Tenant not found in context");
            return false;
        }

        TenantRoleFilter tenantRoleFilter = new TenantRoleFilter();
        tenantRoleFilter.setTenantId(tenantId);

        for (Role role : user.getRoles()) {
            tenantRoleFilter.setRoleId(role.getId());

            List<TenantRoles> tenantRoles = tenantRoleQueryBuilder.search(tenantRoleFilter);
            if (tenantRoles.isEmpty()) continue;

            for (TenantRoles tenantRole : tenantRoles) {
                if (hasMenuAccess(tenantRole.getId(), menu.getId())) {
                    log.info("✅ Access granted to menu: {}", menuName);
                    return true;
                }
            }
        }

        log.info("❌ Access denied to menu: {}", menuName);
        return false;
    }

    @Override
    public boolean canAccess(String menuName, Long userId) {
        Menu menu = menuService.findMenuByName(menuName);
        if (menu == null) {
            log.warn("Menu '{}' not found", menuName);
            return false;
        }

        User user = userService.findUserById(userId);
        if (user == null) {
            return false;
        }

        TenantRoleFilter tenantRoleFilter = new TenantRoleFilter();
        tenantRoleFilter.setTenantId(user.getTenant().getId());

        for (Role role : user.getRoles()) {
            tenantRoleFilter.setRoleId(role.getId());

            List<TenantRoles> tenantRoles = tenantRoleQueryBuilder.search(tenantRoleFilter);
            if (tenantRoles.isEmpty()) continue;

            for (TenantRoles tenantRole : tenantRoles) {
                if (hasMenuAccess(tenantRole.getId(), menu.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getUserPriorityOnMenu(String menuName) {
        Menu menu = menuService.findMenuByName(menuName);
        if (menu == null) {
            log.warn("Menu '{}' not found", menuName);
            return 0;
        }

        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (user == null) {
            log.warn("No logged-in user");
            return 0;
        }

        Long tenantId = TenantContextHolder.getCurrentTenant();
        if (tenantId == null) {
            log.warn("Tenant not found in context");
            return 0;
        }

        int priority = 0;
        TenantRoleFilter tenantRoleFilter = new TenantRoleFilter();
        tenantRoleFilter.setTenantId(tenantId);

        for (Role role : user.getRoles()) {
            tenantRoleFilter.setRoleId(role.getId());

            List<TenantRoles> tenantRoles = tenantRoleQueryBuilder.search(tenantRoleFilter);
            if (tenantRoles == null || tenantRoles.isEmpty()) continue;

            for (TenantRoles tenantRole : tenantRoles) {
                TenantRoleMenuFilter trmFilter = new TenantRoleMenuFilter();
                trmFilter.setTenantRoleId(tenantRole.getId());
                trmFilter.setMenuId(menu.getId());

                List<TenantRoleMenu> trmList = tenantRoleMenuQueryBuilder.search(trmFilter);
                if (trmList == null || trmList.isEmpty()) continue;

                for (TenantRoleMenu trm : trmList) {
                    if (trm.getIsActive()) {
                        priority = Math.max(priority, trm.getPriority());
                    }
                }
            }
        }

        return priority;
    }
}