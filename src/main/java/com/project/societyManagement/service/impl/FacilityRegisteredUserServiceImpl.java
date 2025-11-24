package com.project.societyManagement.service.impl;

import com.project.societyManagement.entity.Facility;
import com.project.societyManagement.entity.FacilityRegisteredUser;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.queryBuilder.facility.FacilityQueryBuilder;
import com.project.societyManagement.queryBuilder.facilityRegisteredUser.FacilityRegisteredUserFilter;
import com.project.societyManagement.queryBuilder.facilityRegisteredUser.FacilityRegisteredUserQueryBuilder;
import com.project.societyManagement.repository.FacilityRegisteredUserRepo;
import com.project.societyManagement.service.FacilityRegisteredUserService;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityRegisteredUserServiceImpl implements FacilityRegisteredUserService {

    private final FacilityRegisteredUserQueryBuilder facilityRegisteredUserQueryBuilder;
    private final FacilityRegisteredUserRepo facilityRegisteredUserRepo;

    public FacilityRegisteredUser registerUserToFacility(Facility facility, User user) {

        FacilityRegisteredUserFilter filter = new FacilityRegisteredUserFilter();
        filter.setFacility(facility.getId());
        filter.setUser(user.getId());
        FacilityRegisteredUser existing = null;
        try {
            existing = facilityRegisteredUserQueryBuilder.findById(filter);
        } catch (Exception ex) {

            existing = null;
        }


        if (existing != null) {
            if (Boolean.TRUE.equals(existing.getIsActive())) {
                throw new EntityExistsException("User is already registered to this facility");
            }
            existing.setIsActive(true);
            return facilityRegisteredUserRepo.save(existing);
        }

        FacilityRegisteredUser registeredUser = FacilityRegisteredUser.builder()
                .facility(facility)
                .user(user)
                .isActive(true)
                .build();


        return facilityRegisteredUserRepo.save(registeredUser);
    }

    public FacilityRegisteredUser unregisterUserFromFacility(Facility facility, User user) {

        FacilityRegisteredUserFilter filter = new FacilityRegisteredUserFilter();
        filter.setFacility(facility.getId());
        filter.setUser(user.getId());
        FacilityRegisteredUser registeredUser = null;
        try{
            registeredUser = facilityRegisteredUserQueryBuilder.findById(filter);
            registeredUser.setIsActive(false);
            return facilityRegisteredUserRepo.save(registeredUser);

        } catch (Exception e) {
            throw new IllegalArgumentException("User is not registered with this Facility.");
        }
    }

    public Page<FacilityRegisteredUser> searchPaginated(FacilityRegisteredUserFilter filter , Pageable pageable){
        return facilityRegisteredUserQueryBuilder.searchPaginated(filter,pageable);
    }



}
