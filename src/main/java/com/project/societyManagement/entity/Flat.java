package com.project.societyManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.societyManagement.entity.common.AuditableEntity;
import com.project.societyManagement.entity.types.FlatCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(
        name = "flats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_flat_tenant_block_number",
                        columnNames = {"tenant_id", "block", "number"}
                )
        }
)
public class Flat extends AuditableEntity {
    private String block;
    private Integer number;
    private Integer floor;
    @Column(name = "sq_ft")
    private Integer sqFt;
    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonIgnore
    private Tenant tenant;
    @OneToMany(mappedBy = "flat", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnore
    private List<FlatMember> members = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private FlatCategory category;
    @OneToMany(mappedBy = "owningFlat", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Vehicle> vehicles = new ArrayList<>();
    @OneToMany(mappedBy = "flat", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ParkingSlot> parkingSlots = new ArrayList<>();

}
