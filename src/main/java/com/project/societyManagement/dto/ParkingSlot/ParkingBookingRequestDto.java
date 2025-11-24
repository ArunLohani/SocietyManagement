package com.project.societyManagement.dto.ParkingSlot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingBookingRequestDto {
    Long parkingSlotId ;
    Long vehicleId;
}
