package com.project.societyManagement.service;

import com.project.societyManagement.entity.Facility;
import com.project.societyManagement.entity.FacilityRegisteredUser;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.queryBuilder.facilityRegisteredUser.FacilityRegisteredUserFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FacilityRegisteredUserService {

    public FacilityRegisteredUser registerUserToFacility(Facility facility, User user);

    public FacilityRegisteredUser unregisterUserFromFacility(Facility facility, User user) ;

    public Page<FacilityRegisteredUser> searchPaginated(FacilityRegisteredUserFilter filter , Pageable pageable);

}
