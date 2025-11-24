package com.project.societyManagement.service;

import com.project.societyManagement.entity.Facility;
import com.project.societyManagement.entity.FacilityBooking;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.BookingStatus;
import com.project.societyManagement.queryBuilder.facilityBooking.FacilityBookingFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

public interface FacilityBookingService {

    public FacilityBooking getBookingById(Long bookingId);

    public FacilityBooking bookFacility(Long facilityId);

    public FacilityBooking approveBooking(Long facilityBookingId);

    public FacilityBooking rejectBooking(Long facilityBookingId , String notes);

    public Page<FacilityBooking> searchPaginated(FacilityBookingFilter filter, Pageable pageable);
}
