package com.project.societyManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private String registrationNumber;
    private String vehicleType;
    private String brand;
    private String model;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

//    @OneToOne
//    @JoinColumn(name = "parking_slot_id")
//    @JsonIgnore
//    private ParkingSlot parkingSlot;
}
