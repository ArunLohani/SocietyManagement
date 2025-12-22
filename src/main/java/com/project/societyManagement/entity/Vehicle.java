package com.project.societyManagement.entity;

import com.project.societyManagement.entity.common.AuditableEntity;
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
@Table(name = "vehicles")
public class Vehicle extends AuditableEntity {

    @Column(name = "registration_number")
    private String registrationNumber;
    @Column(name = "vehicle_type")
    private String vehicleType;
    private String brand;
    private String model;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Flat owningFlat;
//    @Lob
//    @Column(columnDefinition = "BYTEA")
//    private byte[] image;
//    @OneToOne
//    @JoinColumn(name = "parking_slot_id")
//    @JsonIgnore
//    private ParkingSlot parkingSlot;
}
