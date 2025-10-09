package com.project.societyManagement.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController

public class GreetController {

    @GetMapping("/")
    public String greet(){
        return "Hey , Your Backend is Working...";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/greetAdmin")
    public String greetAdmin(){
        return "Hey Admin, Your Backend is Working...";
    }
    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/greetOwner")
    public String greetOwner(){
        return "Hey Owner, Your Backend is Working...";
    }
    @PreAuthorize("hasRole('TENANT')")
    @GetMapping("/greetTenant")
    public String greetTenant(){
        return "Hey Tenant, Your Backend is Working...";
    }
}
