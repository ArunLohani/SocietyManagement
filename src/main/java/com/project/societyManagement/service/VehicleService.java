package com.project.societyManagement.service;

import com.project.societyManagement.dto.Vehicle.VehicleRegisterRequest;
import com.project.societyManagement.dto.Vehicle.VehicleResponse;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.Vehicle;
import com.project.societyManagement.queryBuilder.vehicle.VehicleFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface VehicleService {
    public VehicleResponse toResponse(Vehicle vehicle);
    public Vehicle findVehicleById(Long id);
    public Vehicle registerVehicle(VehicleRegisterRequest vehicleRegisterRequest);
//    public Vehicle uploadVehicleImage(Long id, MultipartFile image) throws IOException;
    public Vehicle updateVehicle(Long vehicleId,VehicleRegisterRequest vehicleRegisterRequest);
    public Vehicle removeVehicle(Long vehicleId);
    public Page<VehicleResponse> searchVehicle(VehicleFilter vehicleFilter , Pageable pageable);
}
