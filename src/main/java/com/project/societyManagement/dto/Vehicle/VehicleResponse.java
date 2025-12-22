package com.project.societyManagement.dto.Vehicle;

import com.project.societyManagement.entity.Flat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {
    private Long id;
    private String registrationNumber;
    private String vehicleType;
    private String brand;
    private String model;
    private Flat owner;
    private String image;
}
