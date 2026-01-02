package com.project.societyManagement.entity;

import com.project.societyManagement.entity.common.AuditableEntity;
import com.project.societyManagement.entity.types.VisitorStatus;
import com.project.societyManagement.entity.types.VisitorType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "visitor_requests")
public class VisitorRequest extends AuditableEntity {

    // Visitor details
    @Column(nullable = false,name = "visitor_name")
    private String visitorName;

    @Column(nullable = false,name = "visitor_phone")
    private String visitorPhone;

    @Column(name = "visitor_email")
    private String visitorEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitorType type;

    private String purpose;
    @Column(name = "rejection_reason")
    private String rejectionReason;
    // Visit timing
    @Column(name = "expected_in")
    private LocalDateTime expectedIn;
    @Column(name = "expected_out")
    private LocalDateTime expectedOut;

    // OTP-based validation
    private String otp;
    @Column(name = "otp_generated_at")
    private LocalDateTime otpGeneratedAt;
    @Column(name = "otp_expires_at")
    private LocalDateTime otpExpiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitorStatus status;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "flat_id", nullable = false)
    private Flat flat;

    @ManyToOne
    @JoinColumn(name = "requested_by_user_id", nullable = false)
    private User requestedBy; // resident or security (for walk-in)

    // Approval & tracking
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    @Column(name = "entered_at")
    private LocalDateTime enteredAt;
    @Column(name = "exited_at")
    private LocalDateTime exitedAt;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = VisitorStatus.PENDING;
        }
    }

}
