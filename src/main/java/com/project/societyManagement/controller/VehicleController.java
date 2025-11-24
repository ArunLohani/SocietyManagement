package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.Vehicle.VehicleRegisterRequest;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.Vehicle;
import com.project.societyManagement.queryBuilder.vehicle.VehicleFilter;
import com.project.societyManagement.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vehicle")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping("/id")
    public ResponseEntity<ApiResponse<Vehicle>> getVehicleById(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.findVehicleById(id);
        ApiResponse<Vehicle> response = new ApiResponse<>(true,"Vehicle has been fetched successfully",vehicle);
        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<Vehicle>> registerVehicle(@RequestBody VehicleRegisterRequest request) {
        Vehicle vehicle = vehicleService.registerVehicle(request);
        ApiResponse<Vehicle> response = new ApiResponse<>(true,"Vehicle has been created successfully",vehicle);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/id")
    public ResponseEntity<ApiResponse<Vehicle>> updateVehicle(@RequestBody VehicleRegisterRequest request,@PathVariable Long id) {
        Vehicle vehicle = vehicleService.updateVehicle(id,request);
        ApiResponse<Vehicle> response = new ApiResponse<>(true,"Vehicle has been updated successfully",vehicle);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<Vehicle>> searchVehicle(@RequestBody VehicleFilter filter,
                                                 @RequestParam(defaultValue = "0") Integer page,
                                                 @RequestParam(defaultValue = "6") Integer limit){
        Pageable pageable = PageRequest.of(page,limit);
        Page<Vehicle> vehicles= vehicleService.searchVehicle(filter,pageable);
        return ResponseEntity.ok(vehicles);
    }

    @DeleteMapping("/id")
    public ResponseEntity<ApiResponse<Vehicle>> unregisterVehicle(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.removeVehicle(id);
        ApiResponse<Vehicle> response = new ApiResponse<>(true,"Vehicle has been removed successfully",vehicle);
        return ResponseEntity.ok(response);
    }
}
