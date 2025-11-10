package com.project.societyManagement.service.impl;
import com.project.societyManagement.entity.Menu;
import com.project.societyManagement.entity.TenantRoleMenu;
import com.project.societyManagement.entity.TenantRoles;
import com.project.societyManagement.queryBuilder.tenantRole.TenantRoleFilter;
import com.project.societyManagement.queryBuilder.tenantRole.TenantRoleQueryBuilder;
import com.project.societyManagement.queryBuilder.tenantRoleMenu.TenantRoleMenuFilter;
import com.project.societyManagement.queryBuilder.tenantRoleMenu.TenantRoleMenuQueryBuilder;
import com.project.societyManagement.repository.TenantRoleMenuRepo;
import com.project.societyManagement.service.MenuService;
import com.project.societyManagement.service.TenantRoleMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    public TenantRoleMenu assignMenuToTenantRole(Long tenantRoleId, Long menuId) {
        log.info("Assigning menu {} to tenant role {}", menuId, tenantRoleId);
        // Fetch tenant role
        TenantRoleFilter tenantRoleFilter = new TenantRoleFilter();
        tenantRoleFilter.setId(tenantRoleId);
        TenantRoles tenantRole = tenantRoleQueryBuilder.findById(tenantRoleFilter);
        // Fetch menu
        Menu menu = menuService.findMenuById(menuId);
        // Check if mapping already exists
        TenantRoleMenuFilter filter = new TenantRoleMenuFilter();
        filter.setTenantRoleId(tenantRoleId);
        filter.setMenuId(menuId);
        List<TenantRoleMenu> existing = tenantRoleMenuQueryBuilder.search(filter);
        if (!existing.isEmpty()) {
            TenantRoleMenu existingMapping = existing.get(0);
            if (!existingMapping.isActive()) {
                existingMapping.setActive(true);
                return tenantRoleMenuRepo.save(existingMapping);
            }
            log.info("Menu already assigned to tenant role");
            return existingMapping;
        }
        // Create new mapping
        TenantRoleMenu tenantRoleMenu = TenantRoleMenu.builder()
                .tenantRoles(tenantRole)
                .menu(menu)
                .build();
        return tenantRoleMenuRepo.save(tenantRoleMenu);
    }

    public void removeMenuFromTenantRole(Long tenantRoleId, Long menuId) {
        log.info("Removing menu {} from tenant role {}", menuId, tenantRoleId);
        TenantRoleMenuFilter filter = new TenantRoleMenuFilter();
        filter.setTenantRoleId(tenantRoleId);
        filter.setMenuId(menuId);
        List<TenantRoleMenu> tenantRoleMenus = tenantRoleMenuQueryBuilder.search(filter);
        if (!tenantRoleMenus.isEmpty()) {
            TenantRoleMenu tenantRoleMenu = tenantRoleMenus.get(0);
            tenantRoleMenu.setActive(false);
            tenantRoleMenuRepo.save(tenantRoleMenu);
            log.info("Menu removed from tenant role successfully");
        } else {
            log.warn("No mapping found between tenant role {} and menu {}", tenantRoleId, menuId);
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
        return !mappings.isEmpty() && mappings.get(0).isActive();
    }

    public TenantRoleMenu searchByTenantRoleAndMenu(Long tenantRoleId, Long menuId){
        TenantRoleMenuFilter filter = new TenantRoleMenuFilter();
        filter.setTenantRoleId(tenantRoleId);
        filter.setMenuId(menuId);
        TenantRoleMenu tenantRoleMenu = tenantRoleMenuQueryBuilder.findById(filter);
        return tenantRoleMenu;
    }
}