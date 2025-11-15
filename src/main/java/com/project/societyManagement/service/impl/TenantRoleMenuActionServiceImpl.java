package com.project.societyManagement.service.impl;

import com.project.societyManagement.entity.*;
import com.project.societyManagement.exception.UserNotFoundException;
import com.project.societyManagement.queryBuilder.action.ActionFilter;
import com.project.societyManagement.queryBuilder.tenantRoleMenu.TenantRoleMenuFilter;
import com.project.societyManagement.queryBuilder.tenantRoleMenu.TenantRoleMenuQueryBuilder;
import com.project.societyManagement.queryBuilder.tenantRoleMenuAction.TenantRoleMenuActionFilter;
import com.project.societyManagement.queryBuilder.tenantRoleMenuAction.TenantRoleMenuActionQueryBuilder;
import com.project.societyManagement.repository.TenantRoleMenuActionRepo;
import com.project.societyManagement.service.ActionService;
import com.project.societyManagement.service.TenantRoleMenuActionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantRoleMenuActionServiceImpl implements TenantRoleMenuActionService {

    private final TenantRoleMenuActionQueryBuilder tenantRoleMenuActionQueryBuilder;
    private final TenantRoleMenuQueryBuilder tenantRoleMenuQueryBuilder;
    private final TenantRoleMenuActionRepo tenantRoleMenuActionRepo;
    private final ActionService actionService;

    /**
     * Assigns an action (and all lower-priority actions) to a TenantRoleMenu.
     */
    @Override
    @Transactional
    public TenantRoleMenuAction assignActionToTenantRoleMenu(Long tenantRoleMenuId, Long actionId) {
        log.info("Assigning action {} to tenant role menu {}", actionId, tenantRoleMenuId);

        TenantRoleMenu tenantRoleMenu = getTenantRoleMenu(tenantRoleMenuId);
        Action targetAction = actionService.findById(actionId);

        // Adjust menu priority if needed
        if (tenantRoleMenu.getPriority() < targetAction.getPriority()) {
            tenantRoleMenu.setPriority(targetAction.getPriority());
        }

        // Fetch all actions with <= target priority
        ActionFilter actionFilter = new ActionFilter();
        actionFilter.setPriority(targetAction.getPriority());
        List<Action> eligibleActions = actionService.getAllActions(actionFilter);

        TenantRoleMenuAction lastAssigned = null;
        for (Action action : eligibleActions) {
            lastAssigned = assignSingleActionIfMissing(tenantRoleMenu, action);
        }

        return lastAssigned;
    }

    /**
     * Removes an action mapping if no higher-priority actions exist.
     */
    @Override
    @Transactional
    public void removeActionFromTenantRoleMenu(Long tenantRoleMenuId, Long actionId) {
        log.info("Removing action {} from tenant role menu {}", actionId, tenantRoleMenuId);

        TenantRoleMenuAction tenantRoleMenuAction = findExistingMapping(tenantRoleMenuId, actionId);

        int menuPriority = tenantRoleMenuAction.getTenantRoleMenu().getPriority();
        int actionPriority = tenantRoleMenuAction.getAction().getPriority();

        if (menuPriority > actionPriority) {
            throw new UserNotFoundException("Cannot disable a lower-priority action while higher priority exists.");
        }

        tenantRoleMenuAction.setActive(false);
        tenantRoleMenuActionRepo.save(tenantRoleMenuAction);
        log.info("Action {} removed from tenant role menu {}", actionId, tenantRoleMenuId);
    }

    /**
     * Returns all active actions for a given TenantRoleMenu.
     */
    @Override
    public List<TenantRoleMenuAction> getActionsForTenantRoleMenu(Long tenantRoleMenuId) {
        TenantRoleMenuActionFilter filter = new TenantRoleMenuActionFilter();
        filter.setTenantRoleMenuId(tenantRoleMenuId);
        return tenantRoleMenuActionQueryBuilder.search(filter);
    }

    @Override
    public List<TenantRoleMenuAction> getTenantRolesMenuForAction(Long actionId) {
        TenantRoleMenuActionFilter filter = new TenantRoleMenuActionFilter();
        filter.setActionId(actionId);
        return tenantRoleMenuActionQueryBuilder.search(filter);
    }

    @Override
    public TenantRoleMenuAction findById(Long id) {
        TenantRoleMenuActionFilter filter = new TenantRoleMenuActionFilter();
        filter.setId(id);
        return tenantRoleMenuActionQueryBuilder.findById(filter);
    }

    @Override
    public boolean hasActionAccess(Long tenantRoleMenuId, Long actionId) {
        TenantRoleMenuActionFilter filter = new TenantRoleMenuActionFilter();
        filter.setTenantRoleMenuId(tenantRoleMenuId);
        filter.setActionId(actionId);
        List<TenantRoleMenuAction> mappings = tenantRoleMenuActionQueryBuilder.search(filter);
        return !mappings.isEmpty() && mappings.get(0).isActive();
    }

    @Override
    public TenantRoleMenuAction searchByTenantRoleMenuAndAction(Long tenantRoleMenuId, Long actionId) {
        TenantRoleMenuActionFilter filter = new TenantRoleMenuActionFilter();
        filter.setTenantRoleMenuId(tenantRoleMenuId);
        filter.setActionId(actionId);
        return tenantRoleMenuActionQueryBuilder.findById(filter);
    }

    // -------------------- 🔒 Private Helpers --------------------

    private TenantRoleMenu getTenantRoleMenu(Long id) {
        TenantRoleMenuFilter filter = new TenantRoleMenuFilter();
        filter.setId(id);
        return tenantRoleMenuQueryBuilder.findById(filter);
    }

    private TenantRoleMenuAction findExistingMapping(Long tenantRoleMenuId, Long actionId) {
        TenantRoleMenuActionFilter filter = new TenantRoleMenuActionFilter();
        filter.setTenantRoleMenuId(tenantRoleMenuId);
        filter.setActionId(actionId);
        List<TenantRoleMenuAction> existing = tenantRoleMenuActionQueryBuilder.search(filter);

        if (existing.isEmpty()) {
            throw new AccessDeniedException("No mapping found for given tenant role menu and action.");
        }
        return existing.get(0);
    }

    private TenantRoleMenuAction assignSingleActionIfMissing(TenantRoleMenu tenantRoleMenu, Action action) {
        TenantRoleMenuActionFilter filter = new TenantRoleMenuActionFilter();
        filter.setTenantRoleMenuId(tenantRoleMenu.getId());
        filter.setActionId(action.getId());
        List<TenantRoleMenuAction> existing = tenantRoleMenuActionQueryBuilder.search(filter);

        if (!existing.isEmpty()) {
            TenantRoleMenuAction existingMapping = existing.get(0);
            if (!existingMapping.isActive()) {
                existingMapping.setActive(true);
                return tenantRoleMenuActionRepo.save(existingMapping);
            }
            return existingMapping;
        }

        TenantRoleMenuAction newMapping = TenantRoleMenuAction.builder()
                .tenantRoleMenu(tenantRoleMenu)
                .action(action)
                .isActive(true)
                .build();

        return tenantRoleMenuActionRepo.save(newMapping);
    }
}
