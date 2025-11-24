package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.entity.FacilityBooking;
import com.project.societyManagement.queryBuilder.facilityBooking.FacilityBookingFilter;
import com.project.societyManagement.service.FacilityBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/facility-booking")
@RequiredArgsConstructor
public class FacilityBookingController {

    private final FacilityBookingService facilityBookingService;

    @GetMapping("/id")
    public ResponseEntity<ApiResponse<FacilityBooking>> getBookingById(@PathVariable Long id){

        FacilityBooking facilityBooking = facilityBookingService.getBookingById(id);
        ApiResponse<FacilityBooking> response = new ApiResponse<>(true,"Booking fetched Successfully",facilityBooking);
        return ResponseEntity.ok(response);


    }
    @PostMapping("/facilityId")
    public ResponseEntity<ApiResponse<FacilityBooking>>  bookFacility(@PathVariable Long facilityId){

        FacilityBooking facilityBooking = facilityBookingService.bookFacility(facilityId);
        ApiResponse<FacilityBooking> response = new ApiResponse<>(true,"Facility Booked Request Successfully",facilityBooking);
        return ResponseEntity.ok(response);

    }

    @PutMapping("accept/facilityBookingId")
    public ResponseEntity<ApiResponse<FacilityBooking>> approveBooking(@PathVariable Long facilityBookingId) {
        FacilityBooking facilityBooking = facilityBookingService.approveBooking(facilityBookingId);
        ApiResponse<FacilityBooking> response = new ApiResponse<>(true, "Booking approved Successfully", facilityBooking);
        return ResponseEntity.ok(response);
    }


    @PutMapping("reject/facilityBookingId")
    public ResponseEntity<ApiResponse<FacilityBooking>> rejectBooking(@PathVariable  Long facilityBookingId ,@RequestBody String notes){
        FacilityBooking facilityBooking = facilityBookingService.rejectBooking(facilityBookingId,notes);
        ApiResponse<FacilityBooking> response = new ApiResponse<>(true, "Booking rejected Successfully", facilityBooking);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<FacilityBooking>> searchPaginated(@RequestBody FacilityBookingFilter filter,
                                                                 @RequestParam(defaultValue = "0")Integer page,
                                                                 @RequestParam(defaultValue = "10")Integer limit
                                                                 ){

        Pageable pageable = PageRequest.of(page,limit);
        Page<FacilityBooking> facilityBookings = facilityBookingService.searchPaginated(filter,pageable);
        return ResponseEntity.ok(facilityBookings);
    }

}
