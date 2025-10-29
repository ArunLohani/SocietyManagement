package com.project.societyManagement.service.impl;

import com.project.societyManagement.entity.Action;
import com.project.societyManagement.entity.Role;
import com.project.societyManagement.queryBuilder.action.ActionFilter;
import com.project.societyManagement.queryBuilder.action.ActionQueryBuilder;
import com.project.societyManagement.queryBuilder.role.RoleFilter;
import com.project.societyManagement.queryBuilder.role.RoleQueryBuilder;
import com.project.societyManagement.repository.RoleRepo;
import com.project.societyManagement.service.ActionService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class ActionServiceImpl implements ActionService {

    private final ActionQueryBuilder actionQueryBuilder;

    @Override
    public Action findByAction(String action) {
        ActionFilter actionFilter = new ActionFilter();
        actionFilter.setAction(action);
        Action actions = actionQueryBuilder.search(actionFilter).get(0);
        return actions;
    }

    @Override
    public Action findById(Long id) {
        ActionFilter actionFilter = new ActionFilter();
        actionFilter.setId(id);
        Action actions = actionQueryBuilder.findById(actionFilter);
        return actions;
    }

    @Override
    public List<Action> getAllActions(){
        ActionFilter actionFilter = new ActionFilter();
        List<Action> actions = actionQueryBuilder.search(actionFilter);
        return actions;
    }

}
