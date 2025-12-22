package com.project.societyManagement.entity;

import com.project.societyManagement.entity.common.AuditableEntity;
import com.project.societyManagement.entity.types.PaymentStatus;
import com.project.societyManagement.entity.types.BillingCycle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "maintenance_payments")
public class MaintenancePayment extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "flat_id", nullable = false)
    private Flat flat;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "billing_start_date", nullable = false)
    private LocalDate billingStartDate;

    @Column(name = "billing_end_date", nullable = false)
    private LocalDate billingEndDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false)
    private BillingCycle billingCycle;

    @Column(name = "months_covered", nullable = false)
    private Integer monthsCovered;

    @Column(name = "monthly_fee", nullable = false)
    private Double monthlyFee;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "base_amount", nullable = false)
    private Double baseAmount; // Original amount without penalty

    @Column(name = "penalty_amount", nullable = false)
    private Double penaltyAmount; // Penalty charged for late payment

    @Column(name = "final_amount", nullable = false)
    private Double finalAmount; // base_amount + penalty_amount

    @Column(name = "is_overdue", nullable = false)
    private Boolean isOverdue; // Flag to track if payment is overdue

    @Column(name = "overdue_days")
    private Integer overdueDays; // Number of days payment is overdue

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_gateway_response", columnDefinition = "TEXT")
    private String paymentGatewayResponse;

    @Column(name = "reference_number")
    private String referenceNumber;
}