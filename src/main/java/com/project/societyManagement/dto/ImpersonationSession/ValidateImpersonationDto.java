package com.project.societyManagement.dto.ImpersonationSession;

import lombok.Data;

@Data
public class ValidateImpersonationDto {
    private boolean isImpersonating;
    private Long sessionId;
    private String superAdminEmail;
    private Boolean isActive;
    private String message;
}
