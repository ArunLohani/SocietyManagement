package com.project.societyManagement.service.impl;

import com.project.societyManagement.annotations.Auditing;
import com.project.societyManagement.entity.Action;
import com.project.societyManagement.queryBuilder.action.ActionFilter;
import com.project.societyManagement.queryBuilder.action.ActionQueryBuilder;
import com.project.societyManagement.service.ActionService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class ActionServiceImpl implements ActionService {

    @Autowired
    private  ActionQueryBuilder actionQueryBuilder;

    @Auditing(entity = "Action",action = "READ")
    @Override
    public Action findByAction(String action) {
        ActionFilter actionFilter = new ActionFilter();
        actionFilter.setAction(action);
        Action actions = actionQueryBuilder.search(actionFilter).get(0);
        return actions;
    }

    @Auditing(entity = "Action",action = "READ")
    @Override
    public Action findById(Long id) {
        ActionFilter actionFilter = new ActionFilter();
        actionFilter.setId(id);
        Action actions = actionQueryBuilder.findById(actionFilter);
        return actions;
    }

    @Auditing(entity = "Action",action = "READ")
    @Override
    public List<Action> getAllActions(ActionFilter actionFilter){

        List<Action> actions = actionQueryBuilder.search(actionFilter);
        return actions;
    }

}
