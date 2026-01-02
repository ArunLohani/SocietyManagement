package com.project.societyManagement.entity;

import com.project.societyManagement.entity.common.AuditableEntity;
import com.project.societyManagement.entity.common.AuditableEntity;
import com.project.societyManagement.entity.types.VisitorStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "visitor_logs")
public class VisitorLog extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "visitor_request_id", nullable = false)
    private VisitorRequest visitorRequest;

    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    @ManyToOne
    @JoinColumn(name = "verified_by_user_id")
    private User verifiedBy; // security user
}

