package com.project.societyManagement.entity;

import com.project.societyManagement.entity.common.AuditableEntity;
import com.project.societyManagement.entity.types.Actions;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "action")
public class Action extends AuditableEntity {
    @Enumerated(EnumType.STRING)
    private Actions action;

    private Integer priority;

    @PrePersist
    @PreUpdate
    private void assignPriority() {
        this.priority = action.getPriority();
    }
}
