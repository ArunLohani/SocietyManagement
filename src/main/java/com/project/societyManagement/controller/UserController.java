package com.project.societyManagement.controller;

import com.project.societyManagement.annotations.RequiresPermission;
import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.User.UserDetails;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @RequiresPermission(api = "CREATE_USER")
    public ResponseEntity<ApiResponse<UserDetails>> findUserById(@PathVariable Long id) {
        log.info("Request received for GET /user/id endpoint.");
        User user = userService.findUserById(id);
        UserDetails response =UserDetails.builder().id(user.getId()).email(user.getEmail()).name(user.getName()).roles(user.getRoles().stream().map(role -> role.getRole()).collect(Collectors.toSet())).build();
        ApiResponse<UserDetails> apiResponse = new ApiResponse(true, "User fetched successfully", response);
        log.info("Response Generated : Login Successful");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<User>> searchUser(@RequestParam(required = false) String name,
                                        @RequestParam(required = false) String email,
                                        @RequestParam(defaultValue = "0") Integer page,
                                        @RequestParam(defaultValue = "6") Integer limit)
    {
        Pageable pageable = PageRequest.of(page,limit);
        Page<User> userPage = userService.searchUser(name,email,pageable);
        return ResponseEntity.ok(userPage);
    }

    @GetMapping("/search-list")
    public ResponseEntity<ApiResponse<List<User>>> searchUserList(@RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String email)
    {

        List<User> users = userService.searchUserList(name,email);
        ApiResponse<List<User>> response = new ApiResponse<>(true,"Users fetched Successfully",users);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDetails>> updateUser(@PathVariable Long id,@RequestBody User user) {
        log.info("Request received for PUT /user/id endpoint.");
        UserDetails updatedUser = userService.updateUser(user);
        ApiResponse<UserDetails> apiResponse = new ApiResponse(true, "User fetched successfully", updatedUser);
        log.info("Response Generated : Update Successful");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/not-assigned")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDetails>>> searchUser()
    {
        List<UserDetails> users = userService.findUsersNotAssignedToTenant();
        ApiResponse<UserDetails> apiResponse = new ApiResponse(true, "User fetched successfully", users);
        return new ResponseEntity(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/checkStatus")
    public ResponseEntity<ApiResponse<Boolean>> checkUserTenantStatus(Authentication authentication)
    {
        Boolean status = userService.checkUserTenanStatus(authentication);
        ApiResponse<UserDetails> apiResponse = new ApiResponse(true, "User Tenant Status fetched successfully", status);
        return new ResponseEntity(apiResponse, HttpStatus.OK);
    }
}
