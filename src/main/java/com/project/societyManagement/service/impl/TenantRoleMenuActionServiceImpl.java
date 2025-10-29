package com.project.societyManagement.service.impl;
import com.project.societyManagement.entity.*;
import com.project.societyManagement.queryBuilder.tenantRole.TenantRoleFilter;
import com.project.societyManagement.queryBuilder.tenantRole.TenantRoleQueryBuilder;
import com.project.societyManagement.queryBuilder.tenantRoleMenu.TenantRoleMenuFilter;
import com.project.societyManagement.queryBuilder.tenantRoleMenu.TenantRoleMenuQueryBuilder;
import com.project.societyManagement.queryBuilder.tenantRoleMenuAction.TenantRoleMenuActionFilter;
import com.project.societyManagement.queryBuilder.tenantRoleMenuAction.TenantRoleMenuActionQueryBuilder;
import com.project.societyManagement.repository.TenantRoleMenuActionRepo;
import com.project.societyManagement.repository.TenantRoleMenuRepo;
import com.project.societyManagement.service.ActionService;
import com.project.societyManagement.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Slf4j
@Service
public class TenantRoleMenuActionServiceImpl  {
    @Autowired
    private TenantRoleMenuActionQueryBuilder tenantRoleMenuActionQueryBuilder;
    @Autowired
    private TenantRoleMenuActionRepo tenantRoleMenuActionRepo;
    @Autowired
    private TenantRoleMenuQueryBuilder tenantRoleMenuQueryBuilder;
    @Autowired
    private ActionService actionService;

    public TenantRoleMenuAction assignActionToTenantRoleMenu(Long tenantRoleMenuId, Long actionId) {
        log.info("Assigning menu {} to tenant role {}", actionId, tenantRoleMenuId);
        // Fetch tenant role menu
        TenantRoleMenuFilter tenantRoleMenuFilter = new TenantRoleMenuFilter();
        tenantRoleMenuFilter.setId(tenantRoleMenuId);
        TenantRoleMenu tenantRoleMenu = tenantRoleMenuQueryBuilder.findById(tenantRoleMenuFilter);
        // Fetch action
        Action action = actionService.findById(actionId);
        // Check if mapping already exists
        TenantRoleMenuActionFilter filter = new TenantRoleMenuActionFilter();
        filter.setTenantRoleMenuId(tenantRoleMenuId);
        filter.setActionId(actionId);
        List<TenantRoleMenuAction> existing = tenantRoleMenuActionQueryBuilder.search(filter);
        if (!existing.isEmpty()) {
            TenantRoleMenuAction existingMapping = existing.get(0);
            if (!existingMapping.isActive()) {
                existingMapping.setActive(true);
                return tenantRoleMenuActionRepo.save(existingMapping);
            }
            log.info("Action already assigned to tenant role menu");
            return existingMapping;
        }
        // Create new mapping
        TenantRoleMenuAction tenantRoleMenuAction = TenantRoleMenuAction.builder()
                .tenantRoleMenu(tenantRoleMenu)
                .action(action)
                .build();
        return tenantRoleMenuActionRepo.save(tenantRoleMenuAction);
    }

    public void removeActionFromTenantRoleMenu(Long tenantRoleMenuId, Long actionId){
        log.info("Removing action {} from tenant role menu {}", actionId, tenantRoleMenuId);
        TenantRoleMenuActionFilter filter = new TenantRoleMenuActionFilter();
        filter.setTenantRoleMenuId(tenantRoleMenuId);
        filter.setActionId(actionId);
        List<TenantRoleMenuAction> tenantRoleMenus = tenantRoleMenuActionQueryBuilder.search(filter);
        if (!tenantRoleMenus.isEmpty()) {
            TenantRoleMenuAction tenantRoleMenu = tenantRoleMenus.get(0);
            tenantRoleMenu.setActive(false);
            tenantRoleMenuActionRepo.save(tenantRoleMenu);
            log.info("Menu removed from tenant role successfully");
        } else {
            log.warn("No mapping found between tenant role {} and menu {}", tenantRoleMenuId, actionId);
        }
    }

    public List<TenantRoleMenuAction> getActionsForTenantRoleMenu(Long tenantRoleMenuId) {
        log.info("Fetching actions for tenant role menu{}", tenantRoleMenuId);
        TenantRoleMenuActionFilter filter = new TenantRoleMenuActionFilter();
        filter.setTenantRoleMenuId(tenantRoleMenuId);
        return tenantRoleMenuActionQueryBuilder.search(filter);
    }

    public List<TenantRoleMenuAction> getTenantRolesMenuForAction(Long actionId) {
        log.info("Fetching tenant roles menu for action {}", actionId);
        TenantRoleMenuActionFilter filter = new TenantRoleMenuActionFilter();
        filter.setActionId(actionId);
        return tenantRoleMenuActionQueryBuilder.search(filter);
    }

    public TenantRoleMenuAction findById(Long id) {
        log.info("Fetching tenant role menu mapping with id {}", id);
        TenantRoleMenuActionFilter filter = new TenantRoleMenuActionFilter();
        filter.setId(id);
        return tenantRoleMenuActionQueryBuilder.findById(filter);
    }

    public boolean hasActionAccess(Long tenantRoleMenuId, Long actionId) {
        TenantRoleMenuActionFilter filter = new TenantRoleMenuActionFilter();
        filter.setTenantRoleMenuId(tenantRoleMenuId);
        filter.setActionId(actionId);
        List<TenantRoleMenuAction> mappings = tenantRoleMenuActionQueryBuilder.search(filter);
        return !mappings.isEmpty() && mappings.get(0).isActive();
    }
}