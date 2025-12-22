package com.project.societyManagement.queryBuilder.vehicle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.societyManagement.entity.ParkingSlot;
import com.project.societyManagement.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleFilter {
        private Long id;
        private String registrationNumber;
        private String vehicleType;
        private String brand;
        private String model;
        private Long owner;
        private Long user;
        private Boolean isActive = true;
}
