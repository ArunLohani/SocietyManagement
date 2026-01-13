package com.project.societyManagement.service;

import com.project.societyManagement.entity.User;

public interface PermissionService {
    public boolean hasPermission(User user, String api);
}
