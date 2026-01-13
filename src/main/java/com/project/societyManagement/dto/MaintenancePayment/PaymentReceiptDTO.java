package com.project.societyManagement.dto.MaintenancePayment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReceiptDTO {
    // Payment Details
    private String referenceNumber;
    private String transactionId;
    private LocalDate paymentDate;
    private String paymentMethod;

    // User Details
    private String userName;
    private String userEmail;
    private String userPhone;

    // Flat Details
    private String flatNumber;
    private String category;

    // Billing Details
    private LocalDate billingStartDate;
    private LocalDate billingEndDate;
    private String billingCycle;
    private Integer monthsCovered;

    // Amount Details
    private Double monthlyFee;
    private Double baseAmount;
    private Double penaltyAmount;
    private Double finalAmount;

    // Overdue Details
    private Boolean isOverdue;
    private Integer overdueDays;

    // Society Details (optional)
    private String societyName;
    private String societyAddress;
}