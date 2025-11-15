package com.project.societyManagement.entity;

import com.project.societyManagement.entity.common.AuditableEntity;
import com.project.societyManagement.entity.types.ComplaintStatus;
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
@Table(name = "complaints")
public class Complaints extends AuditableEntity {

    private String title;
    private String description;
    private String category;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @ManyToOne
    @JoinColumn(name = "raised_by",nullable = false)
    private User raisedByUser;
    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedToUser;
    private ComplaintStatus status = ComplaintStatus.OPEN;
    @Enumerated(EnumType.STRING)
    private Priority priority;
    @JoinColumn(name = "resolution_notes")
    private String resolutionNotes;
}
