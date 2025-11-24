package com.project.societyManagement.controller;
import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.entity.TenantRoleMenuAction;
import com.project.societyManagement.service.TenantRoleMenuActionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/tenantRoleMenuAction")
@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
public class TenantRoleMenuActionController {
    @Autowired
    private TenantRoleMenuActionService tenantRoleMenuActionService;
    // Assign an action to a tenant role menu
    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<TenantRoleMenuAction>> assignActionToTenantRoleMenu(
            @RequestParam Long tenantRoleMenuId,
            @RequestParam Long actionId) {
        TenantRoleMenuAction assigned = tenantRoleMenuActionService.assignActionToTenantRoleMenu(tenantRoleMenuId, actionId);
        ApiResponse<TenantRoleMenuAction> response = new ApiResponse<>(true,
                "Action assigned successfully", assigned);
        return ResponseEntity.ok(response);
    }
    // Remove an action from a tenant role menu (soft delete)
    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<String>> removeActionFromTenantRoleMenu(
            @RequestParam Long tenantRoleMenuId,
            @RequestParam Long actionId) {
        tenantRoleMenuActionService.removeActionFromTenantRoleMenu(tenantRoleMenuId, actionId);
        ApiResponse<String> response = new ApiResponse<>(true,
                "Action removed successfully from tenant role menu", "Removed");
        return ResponseEntity.ok(response);
    }
    // Get all actions for a given tenant role menu
    @GetMapping("/actions/{tenantRoleMenuId}")
    public ResponseEntity<ApiResponse<List<TenantRoleMenuAction>>> getActionsForTenantRoleMenu(@PathVariable Long tenantRoleMenuId) {
        List<TenantRoleMenuAction> actions = tenantRoleMenuActionService.getActionsForTenantRoleMenu(tenantRoleMenuId);
        ApiResponse<List<TenantRoleMenuAction>> response = new ApiResponse<>(true,
                "Actions fetched successfully", actions);
        return ResponseEntity.ok(response);
    }
    // Get all tenant role menus for a given action
    @GetMapping("/tenantRoleMenus/{actionId}")
    public ResponseEntity<ApiResponse<List<TenantRoleMenuAction>>> getTenantRolesMenuForAction(@PathVariable Long actionId) {
        List<TenantRoleMenuAction> tenantRoleMenus = tenantRoleMenuActionService.getTenantRolesMenuForAction(actionId);
        ApiResponse<List<TenantRoleMenuAction>> response = new ApiResponse<>(true,
                "Tenant role menus fetched successfully", tenantRoleMenus);
        return ResponseEntity.ok(response);
    }
    // Get a mapping by its id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantRoleMenuAction>> findById(@PathVariable Long id) {
        TenantRoleMenuAction mapping = tenantRoleMenuActionService.findById(id);
        ApiResponse<TenantRoleMenuAction> response = new ApiResponse<>(true,
                "Mapping fetched successfully", mapping);
        return ResponseEntity.ok(response);
    }
    // Check if a tenant role menu has access to an action
    @GetMapping("/hasAccess")
    public ResponseEntity<ApiResponse<Boolean>> hasActionAccess(
            @RequestParam Long tenantRoleMenuId,
            @RequestParam Long actionId) {
        boolean hasAccess = tenantRoleMenuActionService.hasActionAccess(tenantRoleMenuId, actionId);
        ApiResponse<Boolean> response = new ApiResponse<>(true,
                "Access checked successfully", hasAccess);
        return ResponseEntity.ok(response);
    }


}