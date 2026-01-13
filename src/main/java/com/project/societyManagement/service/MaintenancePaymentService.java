package com.project.societyManagement.service;

import com.project.societyManagement.dto.MaintenancePayment.PaymentCalculationDTO;
import com.project.societyManagement.dto.MaintenancePayment.PaymentRequestDTO;
import com.project.societyManagement.dto.MaintenancePayment.PaymentResponseDTO;
import com.project.societyManagement.entity.Flat;
import com.project.societyManagement.entity.MaintenancePayment;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.BillingCycle;
import com.project.societyManagement.queryBuilder.maintenancePayment.MaintenancePaymentFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import java.time.LocalDate;
import java.util.List;

public interface MaintenancePaymentService {
    public PaymentCalculationDTO calculatePayment(Long flatId , BillingCycle billingCycle);
public PaymentResponseDTO initiatePayment(PaymentRequestDTO paymentRequest , Authentication authentication);
    public PaymentResponseDTO getActivePaymentDetails(Long flatId, LocalDate date);
    public PaymentResponseDTO completePayment(Long paymentId, String paymentMethod,String transactionId, String gatewayResponse);

    public void failPayment(Long paymentId, String reason) ;
    public Integer calculateOverdueDays(LocalDate dueDate);
    public Double calculatePenalty(Flat flat , LocalDate dueDate);
    public Page<MaintenancePayment> searchMaintenancPaymentPaginated(MaintenancePaymentFilter filter, Pageable pageable);
    public List<MaintenancePayment> searchMaintenancePayment(MaintenancePaymentFilter maintenancePaymentFilter);
    public MaintenancePayment findPaymentById(Long paymentId);
    public void sendPaymentReminder(User user);
    public void sendPaymentExpirationReminder(User user);
    public void sendPaymentConfirmation(User user);
    public Boolean checkActivePayment(Long flatId , LocalDate date );
    public void sendPaymentFailure(User user);

}
