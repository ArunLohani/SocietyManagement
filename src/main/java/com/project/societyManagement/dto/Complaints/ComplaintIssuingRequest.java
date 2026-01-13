package com.project.societyManagement.dto.Complaints;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintIssuingRequest {

    @NotEmpty(message = "Title cannot be blank.")
    private String title;
    @NotEmpty(message = "Description cannot be blank.")
    private String description;
    @NotEmpty(message = "Category cannot be blank.")
    private String category;
    @NotEmpty(message = "Raised By User cannot be blank.")
    private Long raisedByUser;
    @NotEmpty(message = "Priority cannot be blank.")
    private String priority;
}
