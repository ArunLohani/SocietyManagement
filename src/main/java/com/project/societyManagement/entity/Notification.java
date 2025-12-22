package com.project.societyManagement.entity;

import com.project.societyManagement.entity.common.AuditableEntity;
import com.project.societyManagement.entity.types.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification extends AuditableEntity {

    private String title;
    private String message;
    private String url;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "tenant_id")
    private Long societyId;
    private boolean read;

}
