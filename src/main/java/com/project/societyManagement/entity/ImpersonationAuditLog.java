package com.project.societyManagement.entity;

import com.project.societyManagement.entity.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "impersonation_audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImpersonationAuditLog extends AuditableEntity {

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "super_admin_id", nullable = false)
    private Long superAdminId;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(name = "action", nullable = false)
    private String action;
    // START, END, EXPIRE, INVALID_ATTEMPT

    @Column(name = "reason")
    private String reason;
    // Optional: "Ticket approved", "Expired", "Manual termination"

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

}
