    package com.project.societyManagement.entity;

    import com.project.societyManagement.entity.common.AuditableEntity;
    import com.project.societyManagement.entity.types.TicketStatus;
    import com.project.societyManagement.entity.types.VisitorStatus;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.sql.Timestamp;
    import java.time.LocalDateTime;

    @Entity
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "support_tickets")
    public class SupportTicket extends AuditableEntity {

        private String title;
        private String description;

        @ManyToOne
        @JoinColumn(name = "raised_by")
        private User raisedBy;

        @Enumerated(EnumType.STRING)
        private TicketStatus status;

        @Column(name = "allow_impersonation")
        private Boolean allowImpersonation;

        @Column(name = "impersonation_until")
        private LocalDateTime impersonationUntil;

        @PrePersist
        protected void onCreate() {
            if (this.status == null) {
                this.status = TicketStatus.OPEN;
            }
            if (this.allowImpersonation == null) {
                this.allowImpersonation = true;
            }
        }


    }
