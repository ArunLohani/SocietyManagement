package com.project.societyManagement.entity;

import com.project.societyManagement.entity.common.AuditableEntity;
import com.project.societyManagement.entity.types.EventStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event")
public class Event extends AuditableEntity {

    private String name;
    private String description;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;
    private String location;
    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.PUBLISHED;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @ManyToOne
    @JoinColumn(name = "organized_by")
    private User OrganizedBy;
    @Column(name = "registration_required")
    private Boolean registrationRequired;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "event_participants",
    joinColumns = @JoinColumn(name = "event_id"),
    inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> participants;
    @Column(name = "max_participants")
    private Integer maxParticipants = Integer.MAX_VALUE;
}
