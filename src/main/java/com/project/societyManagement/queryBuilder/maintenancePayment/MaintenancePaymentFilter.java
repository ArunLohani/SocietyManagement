// ==================== CORRECTED FILTER ====================

package com.project.societyManagement.queryBuilder.maintenancePayment;

import com.project.societyManagement.entity.types.SortFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenancePaymentFilter {
    private Long id;
    private Long flat;
    private Long user;

    // Date range filters
    private LocalDate billingStartDate;
    private LocalDate billingStartDateFrom;
    private LocalDate billingStartDateTo;

    private LocalDate billingEndDate;
    private LocalDate billingEndDateFrom;
    private LocalDate billingEndDateTo;

    private LocalDate paymentDate;
    private LocalDate paymentDateFrom;
    private LocalDate paymentDateTo;

    // Other filters
    private String billingCycle;
    private Integer monthsCovered;
    private Double monthlyFee;
    private Double finalAmount;
    private Double finalAmountMin;
    private Double finalAmountMax;

    private String status;
    private String transactionId;
    private String paymentMethod;
    private String paymentGatewayResponse;
    private String referenceNumber;

    // Special filters for business logic
    private LocalDate activePaymentDate; // Payments active on this date (billingEndDate >= this date)
    private LocalDate expiredBeforeDate; // Payments expired before this date (billingEndDate < this date)
    private Boolean isExpiringSoon; // Combined with daysUntilExpiry
    private Integer daysUntilExpiry; // Used with isExpiringSoon (default 7 days)

    private Boolean isActive = true;

    private SortFilter sortFilter = new SortFilter("createdAt",false);
}



