package com.project.societyManagement.queryBuilder.visitorLogs;

import com.project.societyManagement.entity.Flat;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.SortFilter;
import com.project.societyManagement.entity.types.VisitorStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitorLogsFilter {
    private Long id;

    private LocalDateTime entryTimeTo;
    private LocalDateTime entryTimeFrom;

    private LocalDateTime exitTimeTo;
    private LocalDateTime exitTimeFrom;

    private Long verifiedBy; // security user
    private Boolean isActive = true;
    private SortFilter sortFilter = new SortFilter("createdAt", false);


}
