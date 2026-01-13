package com.project.societyManagement.service;

import com.project.societyManagement.dto.Facility.FacilityCreationRequest;
import com.project.societyManagement.entity.Facility;
import com.project.societyManagement.queryBuilder.facility.FacilityFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FacilityService {

    public Facility findFacilityById(Long id);

    public Facility createFacility(FacilityCreationRequest facilityCreationRequest);

    public Facility updateFacility(Long id,FacilityCreationRequest facilityCreationRequest)
    ;

    public Page<Facility> searchPaginated(FacilityFilter facilityFilter , Pageable pageable);
}
