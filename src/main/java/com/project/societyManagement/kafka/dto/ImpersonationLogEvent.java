package com.project.societyManagement.kafka.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImpersonationLogEvent {

    private Long sessionId;
    private Long ticketId;
    private Long superAdminId;
    private Long adminId;
    private String action;
    private String reason;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

}
