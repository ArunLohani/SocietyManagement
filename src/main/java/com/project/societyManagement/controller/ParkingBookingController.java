package com.project.societyManagement.controller;

import com.project.societyManagement.annotations.RequiresPermission;
import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.ParkingSlot.ParkingBookingRequestDto;
import com.project.societyManagement.entity.ParkingRequest;
import com.project.societyManagement.entity.Vehicle;
import com.project.societyManagement.queryBuilder.parkingRequest.ParkingRequestFilter;
import com.project.societyManagement.service.ParkingRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/parking-booking")
public class ParkingBookingController {
    private final ParkingRequestService parkingRequestService;

    @RequiresPermission(api="SEARCH_PARKING_REQUESTS")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ParkingRequest >> findParkingRequestById(@PathVariable  Long id){
        ParkingRequest parkingRequest = parkingRequestService.findParkingRequestById(id);
        ApiResponse<ParkingRequest> response = new ApiResponse<>(true,"Parking Request fetched successfully",parkingRequest);
        return ResponseEntity.ok(response);
    }

    @RequiresPermission(api="CREATE_PARKING_REQUESTS")
    @PostMapping("/{flatId}")
    public ResponseEntity<ApiResponse<ParkingRequest >> requestParkingSlot(@RequestBody Long parkingSlotId,
                                                                           @PathVariable Long flatId){

        ParkingRequest parkingRequest = parkingRequestService.requestParkingSlot(parkingSlotId,flatId);
        ApiResponse<ParkingRequest> response = new ApiResponse<>(true,"Parking Request generated successfully",parkingRequest);
        return ResponseEntity.ok(response);
    }

    @RequiresPermission(api="CREATE_PARKING_REQUESTS")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/accept/{id}")
    public ResponseEntity<ApiResponse<ParkingRequest >> acceptParkingSlotRequest(@PathVariable  Long id){
        ParkingRequest parkingRequest = parkingRequestService.acceptParkingSlotRequest(id);
        ApiResponse<ParkingRequest> response = new ApiResponse<>(true,"Parking Request accepted successfully",parkingRequest);
        return ResponseEntity.ok(response);
    }
    @RequiresPermission(api="CREATE_PARKING_REQUESTS")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reject/{id}")
    public ResponseEntity<ApiResponse<ParkingRequest >>rejectParkingSlotRequest(@PathVariable  Long id){
        ParkingRequest parkingRequest = parkingRequestService.rejectParkingSlotRequest(id);
        ApiResponse<ParkingRequest> response = new ApiResponse<>(true,"Parking Request rejected successfully",parkingRequest);
        return ResponseEntity.ok(response);
    }
    @RequiresPermission(api="SEARCH_PARKING_REQUESTS")
    @PostMapping("/search")
    public ResponseEntity<Page<ParkingRequest>> searchPaginated(@RequestBody  ParkingRequestFilter filter , @RequestParam(defaultValue = "0") Integer page,
                                                                @RequestParam(defaultValue = "6") Integer limit){

        Pageable pageable = PageRequest.of(page,limit);
        Page<ParkingRequest> requests= parkingRequestService.searchPaginated(filter,pageable);
        return ResponseEntity.ok(requests);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ParkingRequest >> deleteParkingRequest(@PathVariable  Long id){
        ParkingRequest parkingRequest = parkingRequestService.deleteParkingRequest(id);
        ApiResponse<ParkingRequest> response = new ApiResponse<>(true,"Parking Request deleted successfully",parkingRequest);
        return ResponseEntity.ok(response);
    }





}
