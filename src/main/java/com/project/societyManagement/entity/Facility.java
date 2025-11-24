package com.project.societyManagement.entity;

import com.project.societyManagement.entity.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "facility_registered_user",
        uniqueConstraints = @UniqueConstraint(columnNames = {"facility_id", "user_id"})
)
public class Facility extends AuditableEntity {

    @Column(name = "facility_name")
    private String facilityName;

    private Integer capacity;
    private String description;

    @ManyToOne
    @JoinColumn(name = "managed_by")
    private User manager;

    private String location;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "open_for_all")
    private Boolean openForAll;

    @OneToMany(mappedBy = "facility")
    private List<FacilityRegisteredUser> registeredUsers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}
