package com.project.societyManagement.service.impl;

import com.project.societyManagement.dto.MaintenancePayment.PaymentCalculationDTO;
import com.project.societyManagement.dto.MaintenancePayment.PaymentRequestDTO;
import com.project.societyManagement.dto.MaintenancePayment.PaymentResponseDTO;
import com.project.societyManagement.entity.Flat;
import com.project.societyManagement.entity.MaintenancePayment;
import com.project.societyManagement.entity.TenantCategoryPricing;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.BillingCycle;
import com.project.societyManagement.entity.types.PaymentStatus;
import com.project.societyManagement.queryBuilder.maintenancePayment.MaintenancePaymentFilter;
import com.project.societyManagement.queryBuilder.maintenancePayment.MaintenancePaymentQueryBuilder;
import com.project.societyManagement.queryBuilder.tenantCategoryPricing.TenantCategoryPricingFilter;
import com.project.societyManagement.repository.MaintenancePaymentRepo;
import com.project.societyManagement.service.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaintenancePaymentServiceImpl implements MaintenancePaymentService {

    private final MaintenancePaymentQueryBuilder maintenancePaymentQueryBuilder;
    private final MaintenancePaymentRepo maintenancePaymentRepo;
    private final FlatService flatService;
    private final TenantCategoryPricingService tenantCategoryPricingService;
    private final EmailService emailService;
    private final NotificationService notificationService;

    private static final int GRACE_PERIOD_DAYS = 10;

    public MaintenancePayment getActivePaymentForFlat(Long flatId, LocalDate date) {
        MaintenancePaymentFilter filter = new MaintenancePaymentFilter();
        filter.setFlat(flatId);
        filter.setActivePaymentDate(date); // This checks if payment covers this date
        filter.setStatus("COMPLETED");
        filter.setIsActive(true);

        List<MaintenancePayment> activePayments = maintenancePaymentQueryBuilder.search(filter);

        return activePayments.isEmpty() ? null : activePayments.get(0);
    }

    public Boolean checkActivePayment(Long flatId, LocalDate date) {
        MaintenancePayment activePayment = getActivePaymentForFlat(flatId, date);
        return activePayment != null;
    }

    public PaymentResponseDTO getActivePaymentDetails(Long flatId, LocalDate date) {
        MaintenancePayment payment = getActivePaymentForFlat(flatId, date);

        if (payment == null) {
            return null;
        }

        return new PaymentResponseDTO(
                payment.getId(),
                payment.getReferenceNumber(),
                payment.getFinalAmount(),
                payment.getStatus(),
                payment.getPaymentDate(),
                payment.getTransactionId(),
                payment.getBillingStartDate(),
                payment.getBillingEndDate(),
                "Active payment found"
        );
    }

    public MaintenancePayment findPaymentById(Long paymentId){
        MaintenancePaymentFilter maintenancePaymentFilter = new MaintenancePaymentFilter();
        maintenancePaymentFilter.setId(paymentId);
       return maintenancePaymentQueryBuilder.findById(maintenancePaymentFilter);

    }

    public Integer calculateOverdueDays(LocalDate dueDate) {
        LocalDate today = LocalDate.now();

        if (today.isAfter(dueDate)) {
            return (int) ChronoUnit.DAYS.between(dueDate, today);
        }

        return 0;
    }

    public Double calculatePenalty(Flat flat , LocalDate dueDate){

        LocalDate today = LocalDate.now();

        if (today.isAfter(dueDate)){

            long overdueDays = calculateOverdueDays(dueDate);

            if (overdueDays > 0){
                TenantCategoryPricingFilter tenantCategoryPricingFilter = new TenantCategoryPricingFilter();
                tenantCategoryPricingFilter.setTenant(flat.getTenant().getId());
                tenantCategoryPricingFilter.setCategory(flat.getCategory().name());
                List<TenantCategoryPricing> pricingList = tenantCategoryPricingService
                        .searchTenantCategoryPricing(tenantCategoryPricingFilter);

                if(pricingList.isEmpty()) {
                    throw new EntityNotFoundException(
                            "Pricing not configured for category: " + flat.getCategory());
                }

                TenantCategoryPricing tenantCategoryPricing = pricingList.get(0);
                Double penaltyFee = tenantCategoryPricing.getPenaltyFee() == null ? 0.0 : tenantCategoryPricing.getPenaltyFee();

                return penaltyFee * overdueDays;
            }

        }

        return 0.0;
    }

    public PaymentCalculationDTO calculatePayment(Long flatId , BillingCycle billingCycle){
        Flat flat = flatService.getFlatById(flatId);

        // Billing starts from 1st of current month
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);

        // Check if there's already an active payment on the start date
        MaintenancePayment existingPayment = getActivePaymentForFlat(flatId, startDate);
        if (existingPayment != null) {
            throw new IllegalStateException(
                    String.format(
                            "You already have an active payment for this period. " +
                                    "Current payment valid from %s to %s. " +
                                    "Reference: %s",
                            existingPayment.getBillingStartDate(),
                            existingPayment.getBillingEndDate(),
                            existingPayment.getReferenceNumber()
                    )
            );
        }

        TenantCategoryPricingFilter tenantCategoryPricingFilter = new TenantCategoryPricingFilter();
        tenantCategoryPricingFilter.setTenant(flat.getTenant().getId());
        tenantCategoryPricingFilter.setCategory(flat.getCategory().name());
        List<TenantCategoryPricing> pricingList = tenantCategoryPricingService
                .searchTenantCategoryPricing(tenantCategoryPricingFilter);

        if(pricingList.isEmpty()) {
            throw new EntityNotFoundException(
                    "Pricing not configured for category: " + flat.getCategory());
        }

        TenantCategoryPricing tenantCategoryPricing = pricingList.get(0);
        Double monthlyFee = tenantCategoryPricing.getMonthlyFee();
        Integer monthsCovered = billingCycle.getMonths();
        Double baseAmount = monthlyFee * monthsCovered;


        LocalDate endDate = startDate.plusMonths(monthsCovered).minusDays(1);

        // Due date is 10 days from the start of billing cycle
        LocalDate dueDate = startDate.plusDays(GRACE_PERIOD_DAYS);

        // Calculate penalty if already past due date
        Double penalty = calculatePenalty(flat, dueDate);
        Double finalAmount = baseAmount + penalty;

        return PaymentCalculationDTO.builder()
                .monthlyFee(monthlyFee)
                .finalAmount(finalAmount)
                .penalty(penalty)
                .monthsCovered(monthsCovered)
                .billingEndDate(endDate)
                .billingStartDate(startDate)
                .build();
    }

    @Transactional
    public PaymentResponseDTO initiatePayment(PaymentRequestDTO paymentRequest , Authentication authentication){
        User user = (User) authentication.getPrincipal();
        Flat flat = flatService.getFlatById(paymentRequest.getFlatId());

        PaymentCalculationDTO calculatedPayment = calculatePayment(paymentRequest.getFlatId(), paymentRequest.getBillingCycle());

        LocalDate dueDate = calculatedPayment.getBillingStartDate().plusDays(GRACE_PERIOD_DAYS);
        Double penalty = calculatePenalty(flat, dueDate);
        Double baseAmount = calculatedPayment.getFinalAmount() - penalty;
        Integer overdueDays = calculateOverdueDays(dueDate);
        Boolean isOverdue = LocalDate.now().isAfter(dueDate);

        MaintenancePayment payment = MaintenancePayment.builder()
                .billingCycle(paymentRequest.getBillingCycle())
                .billingEndDate(calculatedPayment.getBillingEndDate())
                .billingStartDate(calculatedPayment.getBillingStartDate())
                .dueDate(dueDate)
                .baseAmount(baseAmount)
                .penaltyAmount(penalty)
                .finalAmount(calculatedPayment.getFinalAmount())
                .paymentDate(LocalDate.now())
                .monthsCovered(calculatedPayment.getMonthsCovered())
                .status(PaymentStatus.PENDING)
                .flat(flat)
                .user(user)
                .monthlyFee(calculatedPayment.getMonthlyFee())
                .isOverdue(isOverdue)
                .overdueDays(overdueDays)
                .referenceNumber(generateReferenceNumber())
                .isActive(true)
                .build();

        payment = maintenancePaymentRepo.save(payment);

        return new PaymentResponseDTO(
                payment.getId(),
                payment.getReferenceNumber(),
                payment.getFinalAmount(),
                payment.getStatus(),
                null,
                null,
                payment.getBillingStartDate(),
                payment.getBillingEndDate(),
                "Payment initiated successfully"
        );
    }


    @Transactional
    public PaymentResponseDTO completePayment(Long paymentId,String paymentMethod,String transactionId, String gatewayResponse) {
        MaintenancePayment payment = findPaymentById(paymentId);

        if(payment == null) {
            throw new EntityNotFoundException("Payment not found with id: " + paymentId);
        }

        if(payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Payment already completed");
        }



        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaymentDate(LocalDate.now());
        payment.setTransactionId(transactionId);
        payment.setPaymentGatewayResponse(gatewayResponse);
        payment = maintenancePaymentRepo.save(payment);

        // Send confirmation email
        sendPaymentConfirmation(payment.getUser());

        return new PaymentResponseDTO(
                payment.getId(),
                payment.getReferenceNumber(),
                payment.getFinalAmount(),
                payment.getStatus(),
                payment.getPaymentDate(),
                payment.getTransactionId(),
                payment.getBillingStartDate(),
                payment.getBillingEndDate(),
                "Payment completed successfully"
        );
    }

    @Transactional
    public void failPayment(Long paymentId, String reason) {
        MaintenancePayment payment = findPaymentById(paymentId);

        if(payment == null) {
            throw new EntityNotFoundException("Payment not found with id: " + paymentId);
        }

        if(payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot fail a completed payment");
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setPaymentGatewayResponse(reason);
        maintenancePaymentRepo.save(payment);

        // Send failure notification
        sendPaymentFailure(payment.getUser());
    }

    public List<MaintenancePayment> searchMaintenancePayment(MaintenancePaymentFilter maintenancePaymentFilter){
        return maintenancePaymentQueryBuilder.search(maintenancePaymentFilter);
    }

    public Page<MaintenancePayment> searchMaintenancPaymentPaginated(MaintenancePaymentFilter filter, Pageable pageable){
        return maintenancePaymentQueryBuilder.searchPaginated(filter,pageable);
    }



    private String generateReferenceNumber() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Transactional
    public void sendPaymentReminder(User user){
        String subject = "Maintenance Fee Payment Reminder";
        String message = "Dear " + user.getName() + ",\n\n"
                + "This is a reminder that your maintenance fee for the current billing cycle is pending. "
                + "Please complete the payment at the earliest.\n\n"
                + "Thank you,\nSociety Management System";

        emailService.sendSimpleEmail(user.getEmail(), subject, message);
        notificationService.notifyUser(user.getId(), subject, message);
    }
    @Transactional
    public void sendPaymentExpirationReminder(User user){
        String subject = "Maintenance Fee Expiring Soon – Renewal Reminder";

        String message = "Dear " + user.getName() + ",\n\n"
                + "We hope you are doing well. This is to inform you that your current maintenance fee "
                + "billing period is about to expire soon. Please renew "
                + "your maintenance payment before the due date.\n\n"
                + "If you have already made the payment, please ignore this message.\n\n"
                + "Thank you,\n"
                + "Society Management System";

        emailService.sendSimpleEmail(user.getEmail(), subject, message);
        notificationService.notifyUser(user.getId(), subject, message);
    }

    @Transactional
    public void sendOverdueReminder(User user, Integer overdueDays, Double penalty){
        String subject = "Urgent: Payment Overdue - Day " + overdueDays;
        String message = "Dear " + user.getName() + ",\n\n"
                + "Your maintenance payment is now " + overdueDays + " days overdue. "
                + "Current penalty: ₹" + penalty + "\n\n"
                + "Please complete the payment immediately. Penalty charges increase daily.\n\n"
                + "Thank you,\nSociety Management System";

        emailService.sendSimpleEmail(user.getEmail(), subject, message);
        notificationService.notifyUser(user.getId(), subject, message);
    }

    @Transactional
    public void sendPaymentConfirmation(User user){

        String subject = "Payment Successful – Thank You!";
        String message = "Dear " + user.getName() + ",\n\n"
                + "We have successfully received your maintenance fee payment. "
                + "Thank you for completing the transaction on time.\n\n"
                + "You may view payment details on your dashboard.\n\n"
                + "Regards,\nSociety Management System";

        emailService.sendSimpleEmail(user.getEmail(), subject, message);
        notificationService.notifyUser(user.getId(), subject, message);
    }

    @Transactional
    public void sendPaymentFailure(User user){

        String subject = "Payment Failed – Action Required";
        String message = "Dear " + user.getName() + ",\n\n"
                + "Your recent attempt to pay the maintenance fee was unsuccessful. "
                + "Please try again, or contact support if the issue persists.\n\n"
                + "Thank you,\nSociety Management System";

        emailService.sendSimpleEmail(user.getEmail(), subject, message);
        notificationService.notifyUser(user.getId(), subject, message);
    }


}
