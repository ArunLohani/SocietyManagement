package com.project.societyManagement.dto.ParkingSlot;

import com.project.societyManagement.entity.types.ParkingSlotStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSlotRegisterRequest {
    private String area;
    private String slotNumber;
}
