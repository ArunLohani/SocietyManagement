package com.project.societyManagement.entity;

import com.project.societyManagement.entity.common.AuditableEntity;
import com.project.societyManagement.entity.types.Priority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notice")
public class Notice extends AuditableEntity {
    private String title;
    private String message;
    private String category;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @Column(name = "is_public")
    private Boolean isPublic = true;
    @Column(name = "is_expired")
    private Boolean isExpired = false;
    @Enumerated(EnumType.STRING)
    private Priority priority;
}
