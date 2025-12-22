package com.project.societyManagement.dto.MaintenancePayment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCalculationDTO {
    private Double monthlyFee;
    private Integer monthsCovered;
    private Double finalAmount;
    private Double penalty;
    private LocalDate billingStartDate;
    private LocalDate billingEndDate;
}

