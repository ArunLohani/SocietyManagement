package com.project.societyManagement.queryBuilder.visitorRequest;

import com.project.societyManagement.entity.types.SortFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitorRequestFilter {
    private Long id;
    private String visitorName;
    private String visitorPhone;
    private String visitorEmail;
    private String purpose;

    // Expected In range filters
    private LocalDateTime expectedInFrom;
    private LocalDateTime expectedInTo;

    // Expected Out range filters
    private LocalDateTime expectedOutFrom;
    private LocalDateTime expectedOutTo;

    private String status;
    private Long flat;
    private Long requestedBy; // resident or security (for walk-in)

    // Approved At range filters
    private LocalDateTime approvedAtFrom;
    private LocalDateTime approvedAtTo;

    // Entered At range filters
    private LocalDateTime enteredAtFrom;
    private LocalDateTime enteredAtTo;

    // Exit At range filters
    private LocalDateTime exitedAtFrom;
    private LocalDateTime exitedAtTo;

    // Created At range filters
    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;

    // Updated At range filters
    private LocalDateTime updatedAtFrom;
    private LocalDateTime updatedAtTo;

    private Boolean isActive = true;
    private SortFilter sortFilter = new SortFilter("createdAt", false);
}