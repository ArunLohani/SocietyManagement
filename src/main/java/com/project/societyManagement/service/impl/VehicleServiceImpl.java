package com.project.societyManagement.service.impl;

import com.project.societyManagement.dto.Vehicle.VehicleRegisterRequest;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.Vehicle;
import com.project.societyManagement.queryBuilder.vehicle.VehicleFilter;
import com.project.societyManagement.queryBuilder.vehicle.VehicleQueryBuilder;
import com.project.societyManagement.repository.VehicleRepo;
import com.project.societyManagement.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleQueryBuilder vehicleQueryBuilder;
    private final VehicleRepo vehicleRepo;
    private final ModelMapper modelMapper;

    public Vehicle findVehicleById(Long id){

        VehicleFilter vehicleFilter = new VehicleFilter();
        vehicleFilter.setId(id);
        return vehicleQueryBuilder.findById(vehicleFilter);
    }


    public Vehicle registerVehicle(VehicleRegisterRequest vehicleRegisterRequest){

        Vehicle vehicle = modelMapper.map(vehicleRegisterRequest,Vehicle.class);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        vehicle.setOwner(user);
        return vehicleRepo.save(vehicle);
    }


    public Vehicle updateVehicle(Long vehicleId,VehicleRegisterRequest vehicleRegisterRequest){
        Vehicle vehicle = findVehicleById(vehicleId);
        vehicle.setModel(vehicleRegisterRequest.getModel());
        vehicle.setBrand(vehicleRegisterRequest.getBrand());
        vehicle.setRegistrationNumber(vehicleRegisterRequest.getRegistrationNumber());
        vehicle.setVehicleType(vehicleRegisterRequest.getVehicleType());
        return vehicleRepo.save(vehicle);
    }

    public Vehicle removeVehicle(Long vehicleId){
        Vehicle vehicle = findVehicleById(vehicleId);
        vehicle.setIsActive(false);
        return vehicleRepo.save(vehicle);
    }

    public Page<Vehicle> searchVehicle(VehicleFilter vehicleFilter , Pageable pageable){
        Page<Vehicle> vehicles = vehicleQueryBuilder.searchPaginated(vehicleFilter, pageable);
        return vehicles;
    }



}
