package com.project.societyManagement.dto.Vehicle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.societyManagement.entity.ParkingSlot;
import com.project.societyManagement.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRegisterRequest {

    private String registrationNumber;
    private String vehicleType;
    private String brand;
    private String model;
}
