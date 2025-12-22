package com.project.societyManagement.controller;

import com.project.societyManagement.annotations.RequiresPermission;
import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.Vehicle.VehicleRegisterRequest;
import com.project.societyManagement.dto.Vehicle.VehicleResponse;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.Vehicle;
import com.project.societyManagement.queryBuilder.vehicle.VehicleFilter;
import com.project.societyManagement.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/vehicle")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @RequiresPermission(api = "SEARCH_VEHICLE")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> getVehicleById(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.findVehicleById(id);
        VehicleResponse vehicleResponse = vehicleService.toResponse(vehicle);
        ApiResponse<VehicleResponse> response = new ApiResponse<>(true,"Vehicle has been fetched successfully",vehicleResponse);
        return ResponseEntity.ok(response);
    }
//
//    @RequiresPermission(api = "EDIT_VEHICLE")
//    @PostMapping(value = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<ApiResponse<VehicleResponse>> registerVehicleWithImage(
//            @PathVariable Long id,
//            @RequestParam("image") MultipartFile image) throws IOException {
//
//        // Validate file
//        if (image.isEmpty()) {
//            throw new IllegalArgumentException("Image file is empty");
//        }
//
//        // Optional: Validate file type
//        String contentType = image.getContentType();
//        if (contentType == null || !contentType.startsWith("image/")) {
//            throw new IllegalArgumentException("File must be an image");
//        }
//
//        Vehicle vehicle = vehicleService.uploadVehicleImage(id, image);
//        VehicleResponse vehicleResponse = vehicleService.toResponse(vehicle);
//        ApiResponse<VehicleResponse> response = new ApiResponse<>(
//                true,
//                "Vehicle image uploaded successfully",
//                vehicleResponse
//        );
//        return ResponseEntity.ok(response);
//    }

    @RequiresPermission(api = "CREATE_VEHICLE")
    @PostMapping("")
    public ResponseEntity<ApiResponse<Vehicle>> registerVehicle(@RequestBody VehicleRegisterRequest request) {
        Vehicle vehicle = vehicleService.registerVehicle(request);
        ApiResponse<Vehicle> response = new ApiResponse<>(true,"Vehicle has been created successfully",vehicle);
        return ResponseEntity.ok(response);
    }

    @RequiresPermission(api = "EDIT_VEHICLE")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateVehicle(@RequestBody VehicleRegisterRequest request,@PathVariable Long id) {
        Vehicle vehicle = vehicleService.updateVehicle(id,request);
        VehicleResponse vehicleResponse = vehicleService.toResponse(vehicle);
        ApiResponse<VehicleResponse> response = new ApiResponse<>(true,"Vehicle has been updated successfully",vehicleResponse);
        return ResponseEntity.ok(response);
    }

    @RequiresPermission(api = "SEARCH_VEHICLE")
    @PostMapping("/search")
    public ResponseEntity<Page<VehicleResponse>> searchVehicle(@RequestBody VehicleFilter filter,
                                                 @RequestParam(defaultValue = "0") Integer pageNumber,
                                                 @RequestParam(defaultValue = "6") Integer pageSize){
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Page<VehicleResponse> vehicles= vehicleService.searchVehicle(filter,pageable);
        return ResponseEntity.ok(vehicles);
    }

    @RequiresPermission(api = "CREATE_VEHICLE")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> unregisterVehicle(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.removeVehicle(id);
        VehicleResponse vehicleResponse = vehicleService.toResponse(vehicle);
        ApiResponse<VehicleResponse> response = new ApiResponse<>(true,"Vehicle has been removed successfully",vehicleResponse);
        return ResponseEntity.ok(response);
    }
}
