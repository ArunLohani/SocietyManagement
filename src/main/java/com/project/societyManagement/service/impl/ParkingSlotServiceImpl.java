package com.project.societyManagement.service.impl;

import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.dto.ParkingSlot.ParkingSlotRegisterRequest;
import com.project.societyManagement.entity.*;
import com.project.societyManagement.entity.types.ParkingSlotStatus;
import com.project.societyManagement.queryBuilder.parkingSlot.ParkingSlotFilter;
import com.project.societyManagement.queryBuilder.parkingSlot.ParkingSlotQueryBuilder;
import com.project.societyManagement.repository.ParkingSlotRepo;
import com.project.societyManagement.service.ParkingSlotService;
import com.project.societyManagement.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingSlotServiceImpl implements ParkingSlotService {

    private final ParkingSlotQueryBuilder parkingSlotQueryBuilder;
    private final ParkingSlotRepo parkingSlotRepo;
    private final ModelMapper modelMapper;
    private final TenantService tenantService;

    public  ParkingSlot getParkingSlotById(Long id){
        ParkingSlotFilter parkingSlotFilter = new ParkingSlotFilter();
        parkingSlotFilter.setId(id);
        return parkingSlotQueryBuilder.findById(parkingSlotFilter);
    }

    public ParkingSlot registerParkingSlot(ParkingSlotRegisterRequest parkingSlotRegisterRequest){
        Tenant tenant = tenantService.findTenantById(TenantContextHolder.getCurrentTenant());
        ParkingSlot parkingSlot = ParkingSlot.builder().slotNumber(parkingSlotRegisterRequest.getSlotNumber())
                .area(parkingSlotRegisterRequest.getArea())
                .tenant(tenant)
                .build();

        return parkingSlotRepo.save(parkingSlot);
    }

    public ParkingSlot updateParkingSlot(Long id , ParkingSlotRegisterRequest parkingSlotRegisterRequest){
        ParkingSlot parkingSlot = getParkingSlotById(id);
        parkingSlot.setSlotNumber(parkingSlotRegisterRequest.getSlotNumber());
        parkingSlot.setArea(parkingSlotRegisterRequest.getArea());
        return parkingSlotRepo.save(parkingSlot);
    }

    public ParkingSlot updateSlotStatus(Long id , String status){
        ParkingSlot parkingSlot = getParkingSlotById(id);
        parkingSlot.setStatus(ParkingSlotStatus.valueOf(status));
        return parkingSlotRepo.save(parkingSlot);
    }

    public Page<ParkingSlot> searchPaginated(ParkingSlotFilter parkingSlotFilter, Pageable pageable){
        return parkingSlotQueryBuilder.searchPaginated(parkingSlotFilter,pageable);
    }

    public ParkingSlot deleteParkingSlot(Long id){
        ParkingSlot parkingSlot = getParkingSlotById(id);
        parkingSlot.setIsActive(false);
        return parkingSlotRepo.save(parkingSlot);
    }

    public ParkingSlot reserveParkingSlot(Long id){
        ParkingSlot parkingSlot = getParkingSlotById(id);
        parkingSlot.setStatus(ParkingSlotStatus.RESERVED);
        return parkingSlotRepo.save(parkingSlot);
    }

    public ParkingSlot occupyParkingSlot(Long id, Flat flat){
        ParkingSlot parkingSlot = getParkingSlotById(id);
        if (parkingSlot.getStatus().name() == "RESERVED"){
            throw new IllegalStateException("This Parking is reserved.");
        }
        parkingSlot.setFlat(flat);
        parkingSlot.setStatus(ParkingSlotStatus.OCCUPIED);
        return parkingSlotRepo.save(parkingSlot);
    }
    public ParkingSlot freeParkingSlot(Long id){
        ParkingSlot parkingSlot = getParkingSlotById(id);
        parkingSlot.setFlat(null);
        parkingSlot.setStatus(ParkingSlotStatus.AVAILABLE);
        return parkingSlotRepo.save(parkingSlot);
    }



}
