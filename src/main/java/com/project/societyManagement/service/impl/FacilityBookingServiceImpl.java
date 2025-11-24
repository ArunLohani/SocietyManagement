package com.project.societyManagement.service.impl;

import com.project.societyManagement.entity.Facility;
import com.project.societyManagement.entity.FacilityBooking;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.BookingStatus;
import com.project.societyManagement.queryBuilder.facilityBooking.FacilityBookingFilter;
import com.project.societyManagement.queryBuilder.facilityBooking.FacilityBookingQueryBuilder;
import com.project.societyManagement.repository.FacilityBookingRepo;
import com.project.societyManagement.service.FacilityBookingService;
import com.project.societyManagement.service.FacilityRegisteredUserService;
import com.project.societyManagement.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityBookingServiceImpl implements FacilityBookingService {

    private final FacilityBookingQueryBuilder facilityBookingQueryBuilder;
    private final FacilityBookingRepo facilityBookingRepo;
    private final FacilityService facilityService;
    private final FacilityRegisteredUserService facilityRegisteredUserService;

    @Override
    public FacilityBooking getBookingById(Long bookingId) {
        FacilityBookingFilter filter = new FacilityBookingFilter();
        filter.setId(bookingId);
        FacilityBooking facilityBooking = facilityBookingQueryBuilder.findById(filter);
        return facilityBooking;
    }

    public FacilityBooking bookFacility(Long facilityId){
        Facility facility = facilityService.findFacilityById(facilityId);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        FacilityBooking facilityBooking = FacilityBooking.builder()
                .facility(facility)
                .user(user)
                .build();
        facilityBooking =  facilityBookingRepo.save(facilityBooking);
        if (facility.getOpenForAll() == true){
            facilityBooking = approveBooking(facilityBooking.getId());
        }
        return facilityBooking;
    }

    public FacilityBooking approveBooking(Long facilityBookingId){
        FacilityBookingFilter filter = new FacilityBookingFilter();
        filter.setId(facilityBookingId);
        FacilityBooking facilityBooking = facilityBookingQueryBuilder.findById(filter);
        facilityBooking.setStatus(BookingStatus.APPROVED);
        facilityRegisteredUserService.registerUserToFacility(facilityBooking.getFacility(),facilityBooking.getUser());
        return facilityBookingRepo.save(facilityBooking);
    }

    public FacilityBooking rejectBooking(Long facilityBookingId , String notes){
        FacilityBookingFilter filter = new FacilityBookingFilter();
        filter.setId(facilityBookingId);
        FacilityBooking facilityBooking = facilityBookingQueryBuilder.findById(filter);
        facilityBooking.setAdminComments(notes);
        facilityBooking.setStatus(BookingStatus.REJECTED);
        return facilityBookingRepo.save(facilityBooking);
    }

    public Page<FacilityBooking> searchPaginated(FacilityBookingFilter filter, Pageable pageable){
        return facilityBookingQueryBuilder.searchPaginated(filter,pageable);
    }

}
