package com.project.societyManagement.controller;

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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDetails>> findUserById(@PathVariable Long id) {
        log.info("Request received for GET /user/id endpoint.");
        UserDetails response = userService.findUserById(id);
        ApiResponse<UserDetails> apiResponse = new ApiResponse(true, "User fetched successfully", response);
        log.info("Response Generated : Login Successful");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUser(@RequestParam(required = false) String name,
                                        @RequestParam(required = false) String email,
                                        @RequestParam(defaultValue = "0") Integer page,
                                        @RequestParam(defaultValue = "6") Integer limit)
    {
        Pageable pageable = PageRequest.of(page,limit);
        Page<User> userPage = userService.searchUser(name,email,pageable);
        return ResponseEntity.ok(userPage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDetails>> updateUser(@PathVariable Long id,@RequestBody User user) {
        log.info("Request received for PUT /user/id endpoint.");
        UserDetails userToBeUpdated = userService.findUserById(id);
        UserDetails updatedUser = userService.updateUser(user);
        ApiResponse<UserDetails> apiResponse = new ApiResponse(true, "User fetched successfully", updatedUser);
        log.info("Response Generated : Update Successful");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
