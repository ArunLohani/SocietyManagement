package com.project.societyManagement.service.impl;

import com.project.societyManagement.entity.ParkingRequest;
import com.project.societyManagement.entity.ParkingSlot;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.Vehicle;
import com.project.societyManagement.entity.types.ParkingRequestStatus;
import com.project.societyManagement.queryBuilder.parkingRequest.ParkingRequestFilter;
import com.project.societyManagement.queryBuilder.parkingRequest.ParkingRequestQueryBuilder;
import com.project.societyManagement.repository.ParkingRequestRepo;
import com.project.societyManagement.service.ParkingRequestService;
import com.project.societyManagement.service.ParkingSlotService;
import com.project.societyManagement.service.VehicleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingRequestServiceImpl implements ParkingRequestService {

    private final ParkingRequestQueryBuilder parkingRequestQueryBuilder;
    private final ParkingRequestRepo parkingRequestRepo;
    private final ModelMapper modelMapper;
    private final ParkingSlotService parkingSlotService;
    private final VehicleService vehicleService;

    public ParkingRequest findParkingRequestById(Long id){
        ParkingRequestFilter filter = new ParkingRequestFilter();
        filter.setId(id);
        return parkingRequestQueryBuilder.findById(filter);
    }

    @Transactional
    public ParkingRequest requestParkingSlot(Long parkingSlotId , Long vehicleId){
        ParkingSlot parkingSlot = parkingSlotService.getParkingSlotById(parkingSlotId);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ParkingRequest parkingRequest = ParkingRequest.builder().user(user).requestedSlot(parkingSlot).build();
        return parkingRequestRepo.save(parkingRequest);
    }

    @Transactional
    public ParkingRequest acceptParkingSlotRequest(Long parkingRequestId){
        ParkingRequest parkingRequest = findParkingRequestById(parkingRequestId);
        parkingSlotService.occupyParkingSlot(parkingRequest.getRequestedSlot().getId(),parkingRequest.getUser());
        parkingRequest.setStatus(ParkingRequestStatus.APPROVED);
        return parkingRequestRepo.save(parkingRequest);
    }

    public ParkingRequest rejectParkingSlotRequest(Long parkingRequestId){
        ParkingRequest parkingRequest = findParkingRequestById(parkingRequestId);
        parkingRequest.setStatus(ParkingRequestStatus.REJECTED);
        return parkingRequestRepo.save(parkingRequest);
    }

    public Page<ParkingRequest> searchPaginated(ParkingRequestFilter parkingRequestFilter , Pageable pageable){
        return parkingRequestQueryBuilder.searchPaginated(parkingRequestFilter,pageable);
    }
}
