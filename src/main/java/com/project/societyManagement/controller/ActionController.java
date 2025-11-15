package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.entity.Action;
import com.project.societyManagement.queryBuilder.action.ActionFilter;
import com.project.societyManagement.service.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/action")
public class ActionController {

    @Autowired
    private ActionService actionService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Action>> getActionById(@PathVariable Long id){
        Action action = actionService.findById(id);
        ApiResponse<Action> apiResponse = new ApiResponse<>(true,"Action fetched Successfully",action);
        return  ResponseEntity.ok(apiResponse);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Action>>> getAllAction(){
        ActionFilter actionFilter = new ActionFilter();
        List<Action> actions = actionService.getAllActions(actionFilter);
        ApiResponse<List<Action>> apiResponse = new ApiResponse<>(true,"Actions fetched Successfully",actions);
        return  ResponseEntity.ok(apiResponse);
    }


}
