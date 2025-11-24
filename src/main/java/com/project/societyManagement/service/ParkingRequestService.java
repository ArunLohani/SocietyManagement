package com.project.societyManagement.service;

import com.project.societyManagement.entity.ParkingRequest;
import com.project.societyManagement.entity.ParkingSlot;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.ParkingRequestStatus;
import com.project.societyManagement.queryBuilder.parkingRequest.ParkingRequestFilter;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

public interface ParkingRequestService {
    public ParkingRequest findParkingRequestById(Long id);
    public ParkingRequest requestParkingSlot(Long parkingSlotId , Long vehicleId);
    public ParkingRequest acceptParkingSlotRequest(Long parkingRequestId);
    public ParkingRequest rejectParkingSlotRequest(Long parkingRequestId);
    public Page<ParkingRequest> searchPaginated(ParkingRequestFilter parkingRequestFilter , Pageable pageable);
}
