package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.User.UserDetails;
import com.project.societyManagement.entity.Role;
import com.project.societyManagement.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Role>>> getRoles(){
      List<Role> roles =   roleService.searchRole();
        ApiResponse<String> apiResponse = new ApiResponse(true, "Roles fetched successfully",roles);
        return new ResponseEntity(apiResponse, HttpStatus.OK);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> createRole(@RequestBody String role){
        roleService.createRole(role);
        ApiResponse<String> apiResponse = new ApiResponse(true, "Role created successfully",role + " Role created.");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Role>> findRoleById(@PathVariable Long id) {
        Role response = roleService.findById(id);
        ApiResponse<UserDetails> apiResponse = new ApiResponse(true, "Role fetched successfully", response);
        return new ResponseEntity(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Role>> deleteRoleById(@PathVariable Long id) {
        Role response = roleService.deleteRole(id);
        ApiResponse<UserDetails> apiResponse = new ApiResponse(true, "Role deleted successfully", response);
        return new ResponseEntity(apiResponse, HttpStatus.OK);
    }
}
