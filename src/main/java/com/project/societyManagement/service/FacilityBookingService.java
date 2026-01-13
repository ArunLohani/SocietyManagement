package com.project.societyManagement.service;

import com.project.societyManagement.entity.FacilityBooking;
import com.project.societyManagement.queryBuilder.facilityBooking.FacilityBookingFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FacilityBookingService {

    public FacilityBooking getBookingById(Long bookingId);

    public FacilityBooking bookFacility(Long facilityId);

    public FacilityBooking approveBooking(Long facilityBookingId);

    public FacilityBooking rejectBooking(Long facilityBookingId , String notes);

    public Page<FacilityBooking> searchPaginated(FacilityBookingFilter filter, Pageable pageable);
}
