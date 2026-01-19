package com.project.societyManagement.service.impl;

import com.project.societyManagement.entity.*;
import com.project.societyManagement.entity.types.ParkingRequestStatus;
import com.project.societyManagement.queryBuilder.parkingRequest.ParkingRequestFilter;
import com.project.societyManagement.queryBuilder.parkingRequest.ParkingRequestQueryBuilder;
import com.project.societyManagement.repository.ParkingRequestRepo;
import com.project.societyManagement.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ParkingRequestServiceImpl implements ParkingRequestService {

    private final ParkingRequestQueryBuilder parkingRequestQueryBuilder;
    private final ParkingRequestRepo parkingRequestRepo;
    private final ModelMapper modelMapper;
    private final ParkingSlotService parkingSlotService;
    private final VehicleService vehicleService;
    private final FlatService flatService;
    private final NotificationService notificationService;



    public ParkingRequest findParkingRequestById(Long id){
        ParkingRequestFilter filter = new ParkingRequestFilter();
        filter.setId(id);
        return parkingRequestQueryBuilder.findById(filter);
    }

    public ParkingRequest deleteParkingRequest(Long parkingRequestId) {
        ParkingRequest parkingRequest = findParkingRequestById(parkingRequestId);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (! Objects.equals(parkingRequest.getCreatedBy(), user.getId())) {
            throw new IllegalArgumentException("You can delete only the parking requests created by you.");
        }

        if (parkingRequest.getStatus() != ParkingRequestStatus.PENDING) {
            throw new IllegalArgumentException("Only parking requests in PENDING status can be deleted.");
        }

        parkingRequest.setIsActive(false);
        return parkingRequestRepo.save(parkingRequest);
    }


    @Transactional
    public ParkingRequest requestParkingSlot(Long parkingSlotId , Long flatId){
        ParkingSlot parkingSlot = parkingSlotService.getParkingSlotById(parkingSlotId);
        Flat flat = flatService.getFlatById(flatId);
        ParkingRequest parkingRequest = ParkingRequest.builder().flat(flat).requestedSlot(parkingSlot).status(ParkingRequestStatus.PENDING).build();
        return parkingRequestRepo.save(parkingRequest);
    }

    @Transactional
    public ParkingRequest acceptParkingSlotRequest(Long parkingRequestId){
        ParkingRequest parkingRequest = findParkingRequestById(parkingRequestId);
        parkingSlotService.occupyParkingSlot(parkingRequest.getRequestedSlot().getId(),parkingRequest.getFlat());
        parkingRequest.setStatus(ParkingRequestStatus.APPROVED);
        notificationService.notifyUser(parkingRequest.getCreatedBy(),"Parking Request Approved","Your parking slot request for " + parkingRequest.getRequestedSlot().getArea() + "-" + parkingRequest.getRequestedSlot().getSlotNumber()+ " has been approved. The slot has been assigned to you.","/parking_requests");
        return parkingRequestRepo.save(parkingRequest);
    }

    public ParkingRequest rejectParkingSlotRequest(Long parkingRequestId){
        ParkingRequest parkingRequest = findParkingRequestById(parkingRequestId);
        parkingRequest.setStatus(ParkingRequestStatus.REJECTED);
        notificationService.notifyUser(parkingRequest.getCreatedBy(),"Parking Request Rejected","Your parking slot request for "+ parkingRequest.getRequestedSlot().getArea() + "-" + parkingRequest.getRequestedSlot().getSlotNumber() + " has been rejected. Please contact management for more details.","/parking_requests");
        return parkingRequestRepo.save(parkingRequest);
    }

    public Page<ParkingRequest> searchPaginated(ParkingRequestFilter parkingRequestFilter , Pageable pageable){
        return parkingRequestQueryBuilder.searchPaginated(parkingRequestFilter,pageable);
    }
}
