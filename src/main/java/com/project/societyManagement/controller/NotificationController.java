package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.entity.Notification;
import com.project.societyManagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<Notification>>> getUserNotifications() {

        List<Notification> notifications = notificationService.getUserNotification();
        ApiResponse<List<Notification>> response = new ApiResponse<>(true,"User Notification has been fetched successfully",notifications);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/society")
    public ResponseEntity<ApiResponse<List<Notification>>> getSocietyNotifications() {
        List<Notification> notifications = notificationService.getSocietyNotification();
        ApiResponse<List<Notification>> response = new ApiResponse<>(true,"Society Notification has been fetched successfully",notifications);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable Long id){
        notificationService.markAsRead(id);
        ApiResponse<String> response = new ApiResponse<>(true," Notification has been read successfully"," Notification has been read successfully");
        return ResponseEntity.ok(response);
    }

}
