package com.project.societyManagement.dto.ImpersonationSession;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StartImpersonationResponseDto {
    private Long sessionId;
    private String adminEmail;
    private LocalDateTime expiresAt;
    private Long ticketId;
}
