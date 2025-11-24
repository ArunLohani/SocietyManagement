package com.project.societyManagement.repository;

import com.project.societyManagement.entity.FacilityBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityBookingRepo extends JpaRepository<FacilityBooking,Long> {
}
