package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.Facility.FacilityCreationRequest;
import com.project.societyManagement.entity.Facility;
import com.project.societyManagement.queryBuilder.facility.FacilityFilter;
import com.project.societyManagement.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/facility")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping("/id")
    public ResponseEntity<ApiResponse<Facility>> findFacilityById(@PathVariable  Long id){

        Facility facility = facilityService.findFacilityById(id);
        ApiResponse<Facility> response = new ApiResponse<>(true , "Facility fetched successfully",facility);
        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<Facility>> createFacility(@RequestBody  FacilityCreationRequest facilityCreationRequest){

        Facility facility = facilityService.createFacility(facilityCreationRequest);
        ApiResponse<Facility> response = new ApiResponse<>(true , "Facility created successfully",facility);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/id")
    public ResponseEntity<ApiResponse<Facility>> updateFacility(@PathVariable  Long id,@RequestBody  FacilityCreationRequest facilityCreationRequest)
    {

        Facility facility = facilityService.updateFacility(id,facilityCreationRequest);
        ApiResponse<Facility> response = new ApiResponse<>(true , "Facility updated successfully",facility);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<Facility>> searchPaginated(@RequestBody FacilityFilter facilityFilter,
                                                          @RequestParam(defaultValue = "0") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer limit
                                                          ){
        Pageable pageable = PageRequest.of(page,limit);
        Page<Facility> facilities = facilityService.searchPaginated(facilityFilter,pageable);
        return ResponseEntity.ok(facilities);
    }
}
