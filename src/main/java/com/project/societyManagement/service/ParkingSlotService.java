package com.project.societyManagement.service;

import com.project.societyManagement.dto.ParkingSlot.ParkingSlotRegisterRequest;
import com.project.societyManagement.entity.*;
import com.project.societyManagement.queryBuilder.parkingSlot.ParkingSlotFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParkingSlotService {
    public ParkingSlot getParkingSlotById(Long id);
    public ParkingSlot registerParkingSlot(ParkingSlotRegisterRequest parkingSlotRegisterRequest);
    public ParkingSlot updateParkingSlot(Long id , ParkingSlotRegisterRequest parkingSlotRegisterRequest);
    public ParkingSlot updateSlotStatus(Long id , String status);
    public Page<ParkingSlot> searchPaginated(ParkingSlotFilter parkingSlotFilter, Pageable pageable);
    public ParkingSlot deleteParkingSlot(Long id);
    public ParkingSlot reserveParkingSlot(Long id);
    public ParkingSlot occupyParkingSlot(Long id, Flat flat);
    public ParkingSlot freeParkingSlot(Long id);
}
