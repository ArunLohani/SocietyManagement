package com.project.societyManagement.queryBuilder.parkingSlot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSlotFilter {
    private Long id;
    private String area;
    private String slotNumber;
    private String status;
    private Long flat;
    private Long tenant;
    private Boolean isActive = true;
}
