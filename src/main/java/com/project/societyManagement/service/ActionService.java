package com.project.societyManagement.service;

import com.project.societyManagement.entity.Action;
import com.project.societyManagement.entity.Role;

import java.util.List;

public interface ActionService {

    public Action findByAction(String action);
    public Action findById(Long id);
    public List<Action> getAllActions();
}
