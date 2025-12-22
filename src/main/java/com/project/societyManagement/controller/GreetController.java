package com.project.societyManagement.controller;

import com.project.societyManagement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController

public class GreetController {
    @Autowired
    private NotificationService notificationService;

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

    @GetMapping("/{userId}")
    public String sendNotification(@PathVariable Long userId){
         notificationService.notifyUser(userId,"TITLE","MESSAGE");
         notificationService.notifySociety(1L,"TITLE","MESSAGE");
         return "Notification Send Successfully";
    }
}
