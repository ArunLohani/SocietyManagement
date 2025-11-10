

package com.project.societyManagement.controller;
import com.project.societyManagement.dto.Role.RoleAssignmentRequest;
import com.project.societyManagement.dto.User.UserWithRolesDTO;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.service.UserRoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/user-roles")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
public class UserRoleController {
    @Autowired
    private UserRoleService userRoleService;
    /**
     * Get all users with their roles for a specific tenant
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<List<UserWithRolesDTO>>> getUsersByTenant(
            @PathVariable Long tenantId) {
        List<UserWithRolesDTO> users = userRoleService.getAllUsersWithRolesByTenant(tenantId);
        return ResponseEntity.ok(
                ApiResponse.<List<UserWithRolesDTO>>builder()
                        .data(users)
                        .message("Users with roles retrieved successfully")
                        .success(true)
                        .build()
        );
    }
    /**
     * Assign a role to a user
     */
    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<String>> assignRole(
            @Valid @RequestBody RoleAssignmentRequest request) {
        userRoleService.assignRoleToUser(request.getUserId(), request.getRoleId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<String>builder()
                        .data("Role assigned successfully")
                        .message("Role assigned to user")
                        .success(true)
                        .build()
        );
    }
    /**
     * Remove a role from a user
     */
    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<String>> removeRole(
            @RequestParam Long userId,
            @RequestParam Long roleId) {
        userRoleService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .data("Role removed successfully")
                        .message("Role removed from user")
                        .success(true)
                        .build()
        );
    }
}