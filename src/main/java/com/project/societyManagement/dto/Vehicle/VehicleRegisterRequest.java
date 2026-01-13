package com.project.societyManagement.dto.Vehicle;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRegisterRequest {

    @NotEmpty(message = "Registration Number cannot be null.")
    private String registrationNumber;
    @NotEmpty(message = "Vehicle Type cannot be null.")
    private String vehicleType;
    @NotEmpty(message = "Brand cannot be null.")
    private String brand;
    @NotEmpty(message = "Model cannot be null.")
    private String model;
    @NotEmpty(message = "Flat cannot be null.")
    private Long flat;
}
