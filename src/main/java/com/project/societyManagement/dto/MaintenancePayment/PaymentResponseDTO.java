package com.project.societyManagement.dto.MaintenancePayment;

import com.project.societyManagement.entity.types.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long paymentId;
    private String referenceNumber;
    private Double finalAmount;
    private PaymentStatus status;
    private LocalDate paymentDate;
    private String transactionId;
    private LocalDate billingStartDate;
    private LocalDate billingEndDate;
    private String message;
}