package com.project.societyManagement.queryBuilder.parkingRequest;

import com.project.societyManagement.entity.ParkingSlot;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.Vehicle;
import com.project.societyManagement.entity.types.ParkingRequestStatus;
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
public class ParkingRequestFilter {
    private Long id;
    private Long flat;
    private Long requestedSlot;
    private String status;
    private String adminComment;
    private Boolean isActive = true;
}
