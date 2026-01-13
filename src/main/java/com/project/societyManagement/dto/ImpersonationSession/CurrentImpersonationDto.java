package com.project.societyManagement.dto.ImpersonationSession;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CurrentImpersonationDto {
    private Long sessionId;
    private boolean isImpersonating;
    private String superAdminEmail;
    private String adminEmail;
    private LocalDateTime expiresAt;
    private Long ticketId;
}

