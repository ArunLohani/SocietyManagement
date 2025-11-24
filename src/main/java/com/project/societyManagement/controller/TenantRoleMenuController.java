package com.project.societyManagement.controller;
import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.entity.TenantRoleMenu;
import com.project.societyManagement.service.TenantRoleMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/tenantRoleMenu")
public class TenantRoleMenuController {
    @Autowired
    private TenantRoleMenuService tenantRoleMenuService;
    // Assign menu to tenant role
    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<TenantRoleMenu>> assignMenuToTenantRole(
            @RequestParam Long tenantRoleId,
            @RequestParam Long menuId) {
        TenantRoleMenu assigned = tenantRoleMenuService.assignMenuToTenantRole(tenantRoleId, menuId);
        ApiResponse<TenantRoleMenu> response = new ApiResponse<>(true,
                "Menu assigned successfully", assigned);
        return ResponseEntity.ok(response);
    }
    // Remove menu from tenant role (soft delete)
    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<String>> removeMenuFromTenantRole(
            @RequestParam Long tenantRoleId,
            @RequestParam Long menuId) {
        tenantRoleMenuService.removeMenuFromTenantRole(tenantRoleId, menuId);
        ApiResponse<String> response = new ApiResponse<>(true,
                "Menu removed successfully from tenant role", "Removed");
        return ResponseEntity.ok(response);
    }
    // Get menus for a tenant role
    @GetMapping("/menus/{tenantRoleId}")
    public ResponseEntity<ApiResponse<List<TenantRoleMenu>>> getMenusForTenantRole(@PathVariable Long tenantRoleId) {
        List<TenantRoleMenu> menus = tenantRoleMenuService.getMenusForTenantRole(tenantRoleId);
        ApiResponse<List<TenantRoleMenu>> response = new ApiResponse<>(true,
                "Menus fetched successfully", menus);
        return ResponseEntity.ok(response);
    }
    // Get tenant roles for a menu
    @GetMapping("/tenantRoles/{menuId}")
    public ResponseEntity<ApiResponse<List<TenantRoleMenu>>> getTenantRolesForMenu(@PathVariable Long menuId) {
        List<TenantRoleMenu> tenantRoles = tenantRoleMenuService.getTenantRolesForMenu(menuId);
        ApiResponse<List<TenantRoleMenu>> response = new ApiResponse<>(true,
                "Tenant roles fetched successfully", tenantRoles);
        return ResponseEntity.ok(response);
    }
    // Find mapping by id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantRoleMenu>> findById(@PathVariable Long id) {
        TenantRoleMenu mapping = tenantRoleMenuService.findById(id);
        ApiResponse<TenantRoleMenu> response = new ApiResponse<>(true,
                "Mapping fetched successfully", mapping);
        return ResponseEntity.ok(response);
    }
    // Check if tenant role has access to menu
    @GetMapping("/hasAccess")
    public ResponseEntity<ApiResponse<Boolean>> hasMenuAccess(
            @RequestParam Long tenantRoleId,
            @RequestParam Long menuId) {
        boolean hasAccess = tenantRoleMenuService.hasMenuAccess(tenantRoleId, menuId);
        ApiResponse<Boolean> response = new ApiResponse<>(true,
                "Access checked successfully", hasAccess);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/canAccess")
    public ResponseEntity<ApiResponse<Boolean>> canAccess(
            @RequestParam String menu) {
        boolean hasAccess = tenantRoleMenuService.canAccess(menu);
        ApiResponse<Boolean> response = new ApiResponse<>(true,
                "Access checked successfully", hasAccess);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getPriority/{menu}")
    public ResponseEntity<ApiResponse<Integer>> getPriorityOfMenu(@PathVariable String menu){
        Integer priority = tenantRoleMenuService.getUserPriorityOnMenu(menu);
        ApiResponse<Integer> response = new ApiResponse<>(true,
                "Priority fetched successfully", priority);
        return ResponseEntity.ok(response);
    }

}







