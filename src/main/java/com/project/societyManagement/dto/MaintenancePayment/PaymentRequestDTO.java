    package com.project.societyManagement.dto.MaintenancePayment;

    import com.project.societyManagement.entity.types.BillingCycle;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class PaymentRequestDTO {
        private Long flatId;
        private BillingCycle billingCycle;
//        private String paymentMethod;
    }

