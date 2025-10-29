package com.project.societyManagement.queryBuilder.action;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionFilter {
    private Long id;
    private String action;
    private Boolean isActive;
}
