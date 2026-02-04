    package com.project.societyManagement.entity;

    import com.project.societyManagement.entity.common.AuditableEntity;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import java.time.LocalDateTime;

    @Entity
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "impersonation_session")
    public class ImpersonationSession extends AuditableEntity {

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "ticket_id")
        private SupportTicket ticket;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "admin_id")
        private User admin;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "super_admin_id")
        private User superAdmin;

        @Column(name = "expires_at")
        private LocalDateTime expiresAt;
        @Column(name = "ended_at")
        private LocalDateTime endedAt;

    }
