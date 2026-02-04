package com.project.societyManagement.entity;

import com.project.societyManagement.entity.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "audit_logs")
public class AuditLogs extends AuditableEntity {

    @Column(nullable = false)
    private String entity;
    @Column(nullable = false)
    private String action;
    @Column(name = "entity_id")
    private Long entityId;        // ID of affected entity
    @Column(name = "performed_by")
    private Long performedBy;   // username / userId
    @Column(name = "source")
    private String source;        // REST / GRAPHQL
    @Column(name = "method")
    private String method;        // Service method name
    @Column(name = "event_time", nullable = false)
    private Instant eventTime;

    @PrePersist
    public void prePersist() {
        if (eventTime == null) {
            eventTime = Instant.now();
        }
    }

}
