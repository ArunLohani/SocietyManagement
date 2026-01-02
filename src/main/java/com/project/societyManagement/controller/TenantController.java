package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.User.UserAssignmentRequest;
import com.project.societyManagement.dto.User.UserDetails;
import com.project.societyManagement.entity.Tenant;
import com.project.societyManagement.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tenant")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @GetMapping("")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Tenant>>> getTenants(){
        List<Tenant> tenants =   tenantService.getAllTenants();
        ApiResponse<String> apiResponse = new ApiResponse(true, "Tenants fetched successfully",tenants);
        return new ResponseEntity(apiResponse, HttpStatus.OK);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> createTenant(@RequestBody String tenant){
        tenantService.createTenant(tenant);
        ApiResponse<String> apiResponse = new ApiResponse(true, "Tenant created successfully",tenant + " Tenant created.");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")

    public ResponseEntity<ApiResponse<Tenant>> findTenantById(@PathVariable Long id) {
        Tenant response = tenantService.findTenantById(id);
        ApiResponse<UserDetails> apiResponse = new ApiResponse(true, "Tenant fetched successfully", response);
        return new ResponseEntity(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/addUser")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Tenant>> assignUserToTenant(@RequestBody UserAssignmentRequest request) {
        Tenant response = tenantService.addUserToTenant(request.getTenantId(), request.getUserId());
        ApiResponse<UserDetails> apiResponse = new ApiResponse(true, "User assigned to tenant successfully", response);
        return new ResponseEntity(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/removeUser")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Tenant>> removeUserFromTenant(@RequestBody UserAssignmentRequest request) {
        Tenant response = tenantService.removeUserFromTenant(request.getTenantId(), request.getUserId());
        ApiResponse<UserDetails> apiResponse = new ApiResponse(true, "User removed from tenant successfully", response);
        return new ResponseEntity(apiResponse, HttpStatus.OK);
    }



}
