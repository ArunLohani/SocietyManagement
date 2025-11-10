package com.project.societyManagement.controller;
import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.entity.TenantRoles;
import com.project.societyManagement.service.TenantRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/tenant-roles")
public class TenantRoleController {
    @Autowired
    private TenantRoleService tenantRoleService;
    // Assign a role to a tenant
    @PostMapping("/assign")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> assignRoleToTenant(
            @RequestParam Long tenantId,
            @RequestParam Long roleId) {
        tenantRoleService.assignRoleToTenant(tenantId, roleId);
        ApiResponse<String> response = new ApiResponse<>(true,
                "Role assigned to tenant successfully", "Assigned");
        return ResponseEntity.ok(response);
    }
    // Remove a role from a tenant (soft delete)
    @DeleteMapping("/remove")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> removeRoleFromTenant(
            @RequestParam Long tenantId,
            @RequestParam Long roleId) {
        tenantRoleService.removeRoleFromTenant(tenantId, roleId);
        ApiResponse<String> response = new ApiResponse<>(true,
                "Role removed from tenant successfully", "Removed");
        return ResponseEntity.ok(response);
    }
    // Get all roles for a given tenant
    @GetMapping("/tenant/{tenantId}/roles")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TenantRoles>>> getRolesForTenant(
            @PathVariable Long tenantId) {
        List<TenantRoles> roles = tenantRoleService.getRolesForTenant(tenantId);
        ApiResponse<List<TenantRoles>> response = new ApiResponse<>(true,
                "Roles fetched successfully for tenant", roles);
        return ResponseEntity.ok(response);
    }
    // Get all tenants for a given role
    @GetMapping("/role/{roleId}/tenants")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TenantRoles>>> getTenantsForRole(
            @PathVariable Long roleId) {
        List<TenantRoles> tenants = tenantRoleService.getTenantForRoles(roleId);
        ApiResponse<List<TenantRoles>> response = new ApiResponse<>(true,
                "Tenants fetched successfully for role", tenants);
        return ResponseEntity.ok(response);
    }
    // Get a tenant-role mapping by its id
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TenantRoles>> findById(@PathVariable Long id) {
        TenantRoles mapping = tenantRoleService.findById(id);
        ApiResponse<TenantRoles> response = new ApiResponse<>(true,
                "Tenant-role mapping fetched successfully", mapping);
        return ResponseEntity.ok(response);
    }
}