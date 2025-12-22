package com.project.societyManagement.service;

import com.project.societyManagement.entity.Notification;

import java.util.List;

public interface NotificationService {
    public void notifySociety(Long societyId , String title , String message,String url);
    public void notifyUser(Long userId , String title , String message,String url);
    public void notifySociety(Long societyId , String title , String message);
    public void notifyUser(Long userId , String title , String message);
    public List<Notification> getUserNotification();
    public List<Notification> getSocietyNotification();
    public void markAsRead(Long id);
}
