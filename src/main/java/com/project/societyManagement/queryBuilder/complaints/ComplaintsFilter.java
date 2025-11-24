package com.project.societyManagement.queryBuilder.complaints;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintsFilter {
    private Long id;
    private String title;
    private String description;
    private String category;
    private Long raisedByUser;
    private Long assignedToUser;
    private Boolean isActive = true;
    private Long tenantId;
    private String status;
    private String priority;
}
