package com.project.societyManagement.queryBuilder.facilityBooking;

import com.project.societyManagement.entity.Facility;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.BookingStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityBookingFilter {

    private Long id;
    private Long facility;
    private Long user;
    private BookingStatus status;
    private String adminComments;
}
