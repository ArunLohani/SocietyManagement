package com.project.societyManagement.dto.Notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeCreationRequest {
    private String title;
    private String message;
    private Boolean isPublic = true;
    private Boolean isExpired = false;
    private String category = "Basic";
}
