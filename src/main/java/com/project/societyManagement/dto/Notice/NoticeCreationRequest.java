package com.project.societyManagement.dto.Notice;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeCreationRequest {
    @NotEmpty(message = "Notice Title cannot be empty.")
    private String title;
    @NotEmpty(message = "Notice Message cannot be empty.")
    private String message;
    private Boolean isPublic = true;
    private Boolean isExpired = false;
    private String category = "Basic";
}
