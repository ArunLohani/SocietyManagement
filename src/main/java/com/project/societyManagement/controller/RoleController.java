package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/role")
public class RoleController {

    @PostMapping("")
    public ResponseEntity<ApiResponse<String>> createRole(@RequestBody String role){




    }


}
