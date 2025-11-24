package com.project.societyManagement.service.impl;

import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.dto.Facility.FacilityCreationRequest;
import com.project.societyManagement.entity.Facility;
import com.project.societyManagement.entity.Tenant;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.queryBuilder.facility.FacilityFilter;
import com.project.societyManagement.queryBuilder.facility.FacilityQueryBuilder;
import com.project.societyManagement.repository.FacilityRepo;
import com.project.societyManagement.service.FacilityService;
import com.project.societyManagement.service.TenantService;
import com.project.societyManagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService {

    private final FacilityQueryBuilder facilityQueryBuilder;
    private final FacilityRepo facilityRepo;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final TenantService tenantService;
    public Facility findFacilityById(Long id){
        FacilityFilter filter = new FacilityFilter();
        filter.setId(id);
        return facilityQueryBuilder.findById(filter);
    }

    public Facility createFacility(FacilityCreationRequest facilityCreationRequest)
    {
            Facility facility = modelMapper.map(facilityCreationRequest, Facility.class);
            if (facilityCreationRequest.getManager() != null){
                User user = userService.findUserById(facilityCreationRequest.getManager());
                facility.setManager(user);
            }

        Tenant tenant = tenantService.findTenantById(TenantContextHolder.getCurrentTenant());
            facility.setTenant(tenant);

            return facilityRepo.save(facility);
    }

    public Facility updateFacility(Long id,FacilityCreationRequest facilityCreationRequest)
    {

        Facility facility = findFacilityById(id);
        if (facilityCreationRequest.getManager() != null){
            User user = userService.findUserById(facilityCreationRequest.getManager());
            facility.setManager(user);
        }

        if (facilityCreationRequest.getFacilityName() != null) facility.setFacilityName(facilityCreationRequest.getFacilityName());
        if (facilityCreationRequest.getCapacity() != null) facility.setCapacity(facilityCreationRequest.getCapacity());
        if (facilityCreationRequest.getLocation() != null) facility.setLocation(facilityCreationRequest.getLocation());
        if (facilityCreationRequest.getCloseTime() != null) facility.setCloseTime(facilityCreationRequest.getCloseTime());
        if (facilityCreationRequest.getOpenForAll() != null) facility.setOpenForAll(facilityCreationRequest.getOpenForAll());
        if (facilityCreationRequest.getDescription() != null) facility.setDescription(facilityCreationRequest.getDescription());
        if (facilityCreationRequest.getOpenTime() != null) facility.setOpenTime(facilityCreationRequest.getOpenTime());
        return facilityRepo.save(facility);
    }

    public Page<Facility> searchPaginated(FacilityFilter facilityFilter , Pageable pageable){
        return facilityQueryBuilder.searchPaginated(facilityFilter,pageable);
    }

}
