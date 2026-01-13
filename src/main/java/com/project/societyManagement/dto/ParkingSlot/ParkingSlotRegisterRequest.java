package com.project.societyManagement.dto.ParkingSlot;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSlotRegisterRequest {
    @NotEmpty(message = "Parking Area cannot be empty.")
    private String area;
    @NotEmpty(message = "Parking Slot Number cannot be empty.")
    private String slotNumber;
}
