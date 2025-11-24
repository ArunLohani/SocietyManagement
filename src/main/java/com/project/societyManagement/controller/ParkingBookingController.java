package com.project.societyManagement.controller;

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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/parking-booking")
public class ParkingBookingController {
    private final ParkingRequestService parkingRequestService;

    @GetMapping("/id")
    public ResponseEntity<ApiResponse<ParkingRequest >> findParkingRequestById(@PathVariable  Long id){
        ParkingRequest parkingRequest = parkingRequestService.findParkingRequestById(id);
        ApiResponse<ParkingRequest> response = new ApiResponse<>(true,"Parking Request fetched successfully",parkingRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<ParkingRequest >> requestParkingSlot(@RequestBody ParkingBookingRequestDto parkingBookingRequestDto){

        ParkingRequest parkingRequest = parkingRequestService.requestParkingSlot(parkingBookingRequestDto.getParkingSlotId(), parkingBookingRequestDto.getVehicleId());
        ApiResponse<ParkingRequest> response = new ApiResponse<>(true,"Parking Request generated successfully",parkingRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/accept/id")
    public ResponseEntity<ApiResponse<ParkingRequest >> acceptParkingSlotRequest(@PathVariable  Long id){
        ParkingRequest parkingRequest = parkingRequestService.acceptParkingSlotRequest(id);
        ApiResponse<ParkingRequest> response = new ApiResponse<>(true,"Parking Request accepted successfully",parkingRequest);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/reject/id")
    public ResponseEntity<ApiResponse<ParkingRequest >>rejectParkingSlotRequest(@PathVariable  Long id){
        ParkingRequest parkingRequest = parkingRequestService.rejectParkingSlotRequest(id);
        ApiResponse<ParkingRequest> response = new ApiResponse<>(true,"Parking Request rejected successfully",parkingRequest);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/search")
    public ResponseEntity<Page<ParkingRequest>> searchPaginated(@RequestBody  ParkingRequestFilter parkingRequestFilter , @RequestParam(defaultValue = "0") Integer page,
                                                                @RequestParam(defaultValue = "6") Integer limit){

        Pageable pageable = PageRequest.of(page,limit);
        Page<ParkingRequest> requests= parkingRequestService.searchPaginated(parkingRequestFilter,pageable);
        return ResponseEntity.ok(requests);


    }


}
