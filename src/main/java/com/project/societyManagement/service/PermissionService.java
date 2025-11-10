package com.project.societyManagement.service;

import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.Actions;

public interface PermissionService {
    public boolean hasPermission(User user, String menuName, Actions action);
}
