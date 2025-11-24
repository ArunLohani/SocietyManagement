package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.ParkingSlot.ParkingSlotRegisterRequest;
import com.project.societyManagement.entity.ParkingSlot;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.ParkingSlotStatus;
import com.project.societyManagement.queryBuilder.parkingSlot.ParkingSlotFilter;
import com.project.societyManagement.service.ParkingSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parking-lot")
@RequiredArgsConstructor
public class ParkingSlotController {

    private final ParkingSlotService parkingSlotService;

    @GetMapping("/id")
    public ResponseEntity<ApiResponse<ParkingSlot>> getParkingSlotById(@PathVariable  Long id){

        ParkingSlot parkingSlot = parkingSlotService.getParkingSlotById(id);
        ApiResponse<ParkingSlot> response = new ApiResponse<>(true ,"ParkingLot fetched successfully",parkingSlot);
        return ResponseEntity.ok(response);
    }
    @PostMapping("")
    public ResponseEntity<ApiResponse<ParkingSlot>>  registerParkingSlot(@RequestBody  ParkingSlotRegisterRequest parkingSlotRegisterRequest){
        ParkingSlot parkingSlot = parkingSlotService.registerParkingSlot(parkingSlotRegisterRequest);
        ApiResponse<ParkingSlot> response = new ApiResponse<>(true ,"ParkingLot registered successfully",parkingSlot);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/id")
    public ResponseEntity<ApiResponse<ParkingSlot>>  updateParkingSlot(@PathVariable  Long id , @RequestBody ParkingSlotRegisterRequest parkingSlotRegisterRequest){

        ParkingSlot parkingSlot = parkingSlotService.updateParkingSlot(id,parkingSlotRegisterRequest);
        ApiResponse<ParkingSlot> response = new ApiResponse<>(true ,"ParkingLot registered successfully",parkingSlot);
        return ResponseEntity.ok(response);

    }

    @PutMapping("/id/status")
    public ResponseEntity<ApiResponse<ParkingSlot>>  updateSlotStatus(Long id , ParkingSlotStatus status){
        ParkingSlot parkingSlot = parkingSlotService.updateSlotStatus(id,status);
        ApiResponse<ParkingSlot> response = new ApiResponse<>(true ,"ParkingLot status changed successfully",parkingSlot);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/search")
    public ResponseEntity< Page<ParkingSlot>> searchPaginated(@RequestBody  ParkingSlotFilter parkingSlotFilter,    @RequestParam(defaultValue = "0") Integer page,@RequestParam(defaultValue = "6") Integer limit){

        Pageable pageable = PageRequest.of(page,limit);
        Page<ParkingSlot> parkingSlots = parkingSlotService.searchPaginated(parkingSlotFilter,pageable);
        return ResponseEntity.ok(parkingSlots);
    }
    @DeleteMapping("/id")
    public ResponseEntity<ApiResponse<ParkingSlot>>  deleteParkingSlot(@PathVariable  Long id){

        ParkingSlot parkingSlot = parkingSlotService.deleteParkingSlot(id);
        ApiResponse<ParkingSlot> response = new ApiResponse<>(true ,"ParkingLot status deleted successfully",parkingSlot);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/reserve/id")
    public ResponseEntity<ApiResponse<ParkingSlot>> reserveParkingSlot(Long id){

        ParkingSlot parkingSlot = parkingSlotService.reserveParkingSlot(id);
        ApiResponse<ParkingSlot> response = new ApiResponse<>(true ,"ParkingLot reserved successfully",parkingSlot);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/free/id")
    public ResponseEntity<ApiResponse<ParkingSlot>> freeParkingSlot(Long id){
        ParkingSlot parkingSlot = parkingSlotService.freeParkingSlot(id);
        ApiResponse<ParkingSlot> response = new ApiResponse<>(true ,"ParkingLot reserved successfully",parkingSlot);
        return ResponseEntity.ok(response);
    }



}
