package com.project.societyManagement.service;

import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.dto.ParkingSlot.ParkingSlotRegisterRequest;
import com.project.societyManagement.entity.ParkingSlot;
import com.project.societyManagement.entity.Tenant;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.Vehicle;
import com.project.societyManagement.entity.types.ParkingSlotStatus;
import com.project.societyManagement.queryBuilder.parkingSlot.ParkingSlotFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParkingSlotService {
    public ParkingSlot getParkingSlotById(Long id);
    public ParkingSlot registerParkingSlot(ParkingSlotRegisterRequest parkingSlotRegisterRequest);
    public ParkingSlot updateParkingSlot(Long id , ParkingSlotRegisterRequest parkingSlotRegisterRequest);
    public ParkingSlot updateSlotStatus(Long id , ParkingSlotStatus status);
    public Page<ParkingSlot> searchPaginated(ParkingSlotFilter parkingSlotFilter, Pageable pageable);
    public ParkingSlot deleteParkingSlot(Long id);
    public ParkingSlot reserveParkingSlot(Long id);
    public ParkingSlot occupyParkingSlot(Long id, User user);
    public ParkingSlot freeParkingSlot(Long id);
}
