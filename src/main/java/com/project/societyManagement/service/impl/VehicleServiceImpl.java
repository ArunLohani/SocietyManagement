package com.project.societyManagement.service.impl;

import com.project.societyManagement.dto.Vehicle.VehicleRegisterRequest;
import com.project.societyManagement.dto.Vehicle.VehicleResponse;
import com.project.societyManagement.entity.Flat;
import com.project.societyManagement.entity.Vehicle;
import com.project.societyManagement.queryBuilder.vehicle.VehicleFilter;
import com.project.societyManagement.queryBuilder.vehicle.VehicleQueryBuilder;
import com.project.societyManagement.repository.VehicleRepo;
import com.project.societyManagement.service.FlatService;
import com.project.societyManagement.service.VehicleService;
import com.project.societyManagement.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleQueryBuilder vehicleQueryBuilder;
    private final VehicleRepo vehicleRepo;
    private final ModelMapper modelMapper;
    private final FlatService flatService;
    private final ValidationUtil validationUtil;

    public VehicleResponse toResponse(Vehicle vehicle) {
        VehicleResponse res = new VehicleResponse();

        res.setId(vehicle.getId());
        res.setRegistrationNumber(vehicle.getRegistrationNumber());
        res.setVehicleType(vehicle.getVehicleType());
        res.setBrand(vehicle.getBrand());
        res.setModel(vehicle.getModel());
        res.setOwner(vehicle.getOwningFlat());
        return res;
    }

    public Vehicle findVehicleById(Long id){

        VehicleFilter vehicleFilter = new VehicleFilter();
        vehicleFilter.setId(id);
        return vehicleQueryBuilder.findById(vehicleFilter);
    }
//    public Vehicle uploadVehicleImage(Long id,MultipartFile image) throws IOException {
//        Vehicle vehicle = findVehicleById(id);
//        vehicle.setImage(image.getInputStream().readAllBytes());
//        return vehicleRepo.save(vehicle);
//    }

    public Vehicle registerVehicle(VehicleRegisterRequest vehicleRegisterRequest ){
        validationUtil.validate(vehicleRegisterRequest);
        Vehicle vehicle = modelMapper.map(vehicleRegisterRequest,Vehicle.class);
        Flat flat = flatService.getFlatById(vehicleRegisterRequest.getFlat());
        vehicle.setOwningFlat(flat);
        return vehicleRepo.save(vehicle);
    }

    public Vehicle updateVehicle(Long vehicleId,VehicleRegisterRequest vehicleRegisterRequest){
        validationUtil.validate(vehicleRegisterRequest);
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

    public Page<VehicleResponse> searchVehicle(VehicleFilter vehicleFilter , Pageable pageable){
        Page<Vehicle> vehicles = vehicleQueryBuilder.searchPaginated(vehicleFilter, pageable);
        return vehicles.map(this::toResponse);
    }

}
