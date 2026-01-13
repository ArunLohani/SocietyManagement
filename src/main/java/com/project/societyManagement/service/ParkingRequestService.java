package com.project.societyManagement.service;

import com.project.societyManagement.entity.ParkingRequest;
import com.project.societyManagement.queryBuilder.parkingRequest.ParkingRequestFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParkingRequestService {
    public ParkingRequest findParkingRequestById(Long id);
    public ParkingRequest requestParkingSlot(Long parkingSlotId,Long flatId);
    public ParkingRequest acceptParkingSlotRequest(Long parkingRequestId);
    public ParkingRequest rejectParkingSlotRequest(Long parkingRequestId);
    public Page<ParkingRequest> searchPaginated(ParkingRequestFilter parkingRequestFilter , Pageable pageable); public ParkingRequest deleteParkingRequest(Long parkingRequestId);
}
