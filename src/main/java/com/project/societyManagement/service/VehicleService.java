package com.project.societyManagement.service;

import com.project.societyManagement.dto.Vehicle.VehicleRegisterRequest;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.Vehicle;
import com.project.societyManagement.queryBuilder.vehicle.VehicleFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

public interface VehicleService {

    public Vehicle findVehicleById(Long id);
    public Vehicle registerVehicle(VehicleRegisterRequest vehicleRegisterRequest);
    public Vehicle updateVehicle(Long vehicleId,VehicleRegisterRequest vehicleRegisterRequest);
    public Vehicle removeVehicle(Long vehicleId);
    public Page<Vehicle> searchVehicle(VehicleFilter vehicleFilter , Pageable pageable);

}
