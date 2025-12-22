package com.project.societyManagement.queryBuilder.event;


import com.project.societyManagement.entity.types.SortFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFilter {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Long organizedBy;
    private Boolean isActive = true;
    private String status;
    private String location;
    private Long tenantId;
    private SortFilter sortFilter = new SortFilter("createdAt",false);
}
