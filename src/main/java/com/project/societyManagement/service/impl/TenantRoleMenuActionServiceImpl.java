package com.project.societyManagement.service.impl;

import com.project.societyManagement.entity.*;
import com.project.societyManagement.exception.UserNotFoundException;
import com.project.societyManagement.queryBuilder.action.ActionFilter;
import com.project.societyManagement.queryBuilder.tenantRoleMenu.TenantRoleMenuFilter;
import com.project.societyManagement.queryBuilder.tenantRoleMenu.TenantRoleMenuQueryBuilder;
import com.project.societyManagement.queryBuilder.tenantRoleMenuAction.TenantRoleMenuActionFilter;
import com.project.societyManagement.queryBuilder.tenantRoleMenuAction.TenantRoleMenuActionQueryBuilder;
import com.project.societyManagement.repository.TenantRoleMenuActionRepo;
import com.project.societyManagement.repository.TenantRoleMenuRepo;
import com.project.societyManagement.service.ActionService;
import com.project.societyManagement.service.TenantRoleMenuActionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantRoleMenuActionServiceImpl implements TenantRoleMenuActionService {

    private final TenantRoleMenuActionQueryBuilder tenantRoleMenuActionQueryBuilder;
    private final TenantRoleMenuQueryBuilder tenantRoleMenuQueryBuilder;
    private final TenantRoleMenuActionRepo tenantRoleMenuActionRepo;
    private final TenantRoleMenuRepo tenantRoleMenuRepo; // <--- new
    private final ActionService actionService;

    /**
     * Assigns an action (and all lower-priority actions) to a TenantRoleMenu.
     * Updates TenantRoleMenu.priority to the highest active action priority.
     */
    @Override
    @Transactional
    public TenantRoleMenuAction assignActionToTenantRoleMenu(Long tenantRoleMenuId, Long actionId) {
        log.info("Assigning action {} to tenant role menu {}", actionId, tenantRoleMenuId);

        TenantRoleMenu tenantRoleMenu = getTenantRoleMenu(tenantRoleMenuId);
        Action targetAction = actionService.findById(actionId);


        int newMenuPriority = Math.max(tenantRoleMenu.getPriority(),
                targetAction.getPriority());
        tenantRoleMenu.setPriority(newMenuPriority);

        // Fetch all actions with priority <= target priority to assign them (inclusive)
        ActionFilter actionFilter = new ActionFilter();
        actionFilter.setPriority(targetAction.getPriority());
        List<Action> eligibleActions = actionService.getAllActions(actionFilter);

        TenantRoleMenuAction lastAssigned = null;
        for (Action action : eligibleActions) {
            lastAssigned = assignSingleActionIfMissing(tenantRoleMenu, action);
        }

        // Persist possible tenantRoleMenu priority change
        tenantRoleMenuRepo.save(tenantRoleMenu);

        return lastAssigned;
    }

    /**
     * Removes an action mapping and recomputes TenantRoleMenu.priority from remaining active actions.
     */
    @Override
    @Transactional
    public void removeActionFromTenantRoleMenu(Long tenantRoleMenuId, Long actionId) {
        log.info("Removing action {} from tenant role menu {}", actionId, tenantRoleMenuId);

        TenantRoleMenuAction tenantRoleMenuAction = findExistingMapping(tenantRoleMenuId, actionId);

        Action actionToRemove = tenantRoleMenuAction.getAction();

        if (actionToRemove != null && "READ".equalsIgnoreCase(actionToRemove.getAction().toString())){
                throw new IllegalStateException("READ Action is mandatory and cannot be unassigned from menu.");
        }

        if (actionToRemove != null && actionToRemove.getPriority() < tenantRoleMenuAction.getTenantRoleMenu().getPriority()){
            throw new IllegalStateException("Cannot unassign this action because a higher-priority action already exists.");
        }

        // Deactivate the mapping
        tenantRoleMenuAction.setIsActive(false);
        tenantRoleMenuActionRepo.save(tenantRoleMenuAction);
        log.info("Action {} deactivated for tenant role menu {}", actionId, tenantRoleMenuId);

        // Recompute maximum priority among remaining active actions for this tenantRoleMenu
        TenantRoleMenu tenantRoleMenu = tenantRoleMenuAction.getTenantRoleMenu();
        TenantRoleMenuActionFilter filter = new TenantRoleMenuActionFilter();
        filter.setTenantRoleMenuId(tenantRoleMenu.getId());

        List<TenantRoleMenuAction> remaining = tenantRoleMenuActionQueryBuilder.search(filter);

        // Filter active actions and compute max priority; treat null as 0
        Optional<Integer> maxPriorityOpt = remaining.stream()
                .filter(TenantRoleMenuAction::getIsActive)
                .map(trmAction -> trmAction.getAction() != null ? trmAction.getAction().getPriority() : 0)
                .max(Comparator.naturalOrder());

        int recomputedPriority = maxPriorityOpt.orElse(0);
        tenantRoleMenu.setPriority(recomputedPriority);
        tenantRoleMenuRepo.save(tenantRoleMenu);

        log.info("TenantRoleMenu {} priority recomputed to {}", tenantRoleMenu.getId(), recomputedPriority);
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
        return !mappings.isEmpty() && mappings.get(0).getIsActive();
    }

    @Override
    public TenantRoleMenuAction searchByTenantRoleMenuAndAction(Long tenantRoleMenuId, Long actionId) {
        TenantRoleMenuActionFilter filter = new TenantRoleMenuActionFilter();
        filter.setTenantRoleMenuId(tenantRoleMenuId);
        filter.setActionId(actionId);
        return tenantRoleMenuActionQueryBuilder.findById(filter);
    }

    // -------------------- :lock: Private Helpers --------------------

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
            if (!existingMapping.getIsActive()) {
                existingMapping.setIsActive(true);
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