package com.project.societyManagement.dto.Complaints;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintIssuingRequest {
    private String title;
    private String description;
    private String category;
    private Long raisedByUser;
    private String priority;
}
