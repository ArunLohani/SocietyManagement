package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.MaintenancePayment.*;
import com.project.societyManagement.dto.Razorpay.PaymentVerificationDTO;
import com.project.societyManagement.dto.Razorpay.RazorpayOrderDTO;
import com.project.societyManagement.entity.MaintenancePayment;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.BillingCycle;
import com.project.societyManagement.queryBuilder.maintenancePayment.MaintenancePaymentFilter;
import com.project.societyManagement.scheduler.PaymentReminderScheduler;
import com.project.societyManagement.service.MaintenancePaymentService;
import com.project.societyManagement.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/maintenance-payments")
@RequiredArgsConstructor
@Slf4j
public class MaintenancePaymentController {

    private final MaintenancePaymentService maintenancePaymentService;
    private final PaymentReminderScheduler paymentReminderScheduler;
    private final RazorpayService razorpayService;

    /**
     * Calculate payment amount for a flat based on billing cycle
     */
    @GetMapping("/calculate")
    public ResponseEntity<ApiResponse<PaymentCalculationDTO>> calculatePayment(
            @RequestParam Long flatId,
            @RequestParam BillingCycle billingCycle) {
        PaymentCalculationDTO calculation = maintenancePaymentService.calculatePayment(flatId, billingCycle);
        ApiResponse<PaymentCalculationDTO>response = new ApiResponse<>(true,"Payment Calculated Successfully.",calculation);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/active-payment/{id}")
    public ResponseEntity<ApiResponse<Boolean>> checkActivePayment(@PathVariable Long id ,@RequestBody LocalDate date){
        Boolean hasActivePayment = maintenancePaymentService.checkActivePayment(id,date);
        ApiResponse<Boolean>response = new ApiResponse<>(true,"Payment Calculated Successfully.",hasActivePayment);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/get/active-payment/{id}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> getActivePayment(@PathVariable Long id ,@RequestBody LocalDate date){
        PaymentResponseDTO activePayment = maintenancePaymentService.getActivePaymentDetails(id,date);
        ApiResponse<PaymentResponseDTO>response = new ApiResponse<>(true,"Payment Calculated Successfully.",activePayment);
        return ResponseEntity.ok(response);
    }


    /**
     * Initiate a new payment and create Razorpay order
     */
    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<RazorpayOrderDTO>> initiatePayment(
            @RequestBody PaymentRequestDTO paymentRequest,
            Authentication authentication) {
        try {
            log.info("Initiating payment for flat: {}", paymentRequest.getFlatId());

            // Create payment record in your system
            PaymentResponseDTO initiatedPayment = maintenancePaymentService.initiatePayment(
                    paymentRequest, authentication);

            log.info("Payment initiated with ID: {}", initiatedPayment.getPaymentId());

            // Create Razorpay order
            RazorpayOrderDTO razorpayOrder = razorpayService.createOrder(
                    initiatedPayment.getPaymentId(),
                    initiatedPayment.getFinalAmount(),
                    "INR"
            );

            log.info("Razorpay order created: {}", razorpayOrder.getOrderId());

            ApiResponse<RazorpayOrderDTO> response = new ApiResponse<>(
                    true,
                    "Payment initiated and Razorpay order created successfully.",
                    razorpayOrder
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error initiating payment: {}", e.getMessage(), e);
            ApiResponse<RazorpayOrderDTO> response = new ApiResponse<>(
                    false,
                    "Failed to initiate payment: " + e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Verify and complete payment after Razorpay success
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> verifyPayment(
            @RequestBody PaymentVerificationDTO verificationDTO) {
        try {
            log.info("Verifying payment - Order ID: {}, Payment ID: {}",
                    verificationDTO.getRazorpayOrderId(),
                    verificationDTO.getRazorpayPaymentId());

            // Verify signature
            boolean isValid = razorpayService.verifyPaymentSignature(verificationDTO);

            if (!isValid) {
                log.error("Invalid payment signature");
                ApiResponse<PaymentResponseDTO> response = new ApiResponse<>(
                        false,
                        "Invalid payment signature. Payment verification failed.",
                        null
                );
                return ResponseEntity.badRequest().body(response);
            }

            log.info("Payment signature verified successfully");

            // Get payment details from Razorpay to extract notes
            com.razorpay.Payment razorpayPayment = razorpayService.getPaymentDetails(
                    verificationDTO.getRazorpayPaymentId()
            );

            // Extract payment ID from Razorpay notes
            JSONObject notes = new JSONObject(razorpayPayment.get("notes").toString());
            Long paymentId = notes.getLong("payment_id");
            String paymentMethod = razorpayPayment.get("method");
            log.info("Completing payment ID: {}", paymentId);

            // Complete payment in your system
            PaymentResponseDTO completedPayment = maintenancePaymentService.completePayment(

                    paymentId,
                    paymentMethod,
                    verificationDTO.getRazorpayPaymentId(),
                    razorpayPayment.toString()
            );

            log.info("Payment completed successfully: {}", paymentId);

            ApiResponse<PaymentResponseDTO> response = new ApiResponse<>(
                    true,
                    "Payment verified and completed successfully.",
                    completedPayment
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Payment verification failed: {}", e.getMessage(), e);
            ApiResponse<PaymentResponseDTO> response = new ApiResponse<>(
                    false,
                    "Payment verification failed: " + e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Manual payment failure endpoint (if user reports failed payment)
     */
    @PostMapping("/report-failure")
    public ResponseEntity<ApiResponse<String>> reportPaymentFailure(
            @RequestParam Long paymentId,
            @RequestParam String razorpayPaymentId,
            @RequestParam(required = false) String reason) {
        try {
            log.info("Payment failure reported - Payment ID: {}, Razorpay Payment ID: {}",
                    paymentId, razorpayPaymentId);

            // Optionally verify the payment status from Razorpay
            try {
                com.razorpay.Payment payment = razorpayService.getPaymentDetails(razorpayPaymentId);
                String status = payment.get("status");
                log.info("Razorpay payment status: {}", status);

                if ("failed".equals(status)) {
                    String errorCode = payment.has("error_code") ? payment.get("error_code") : "UNKNOWN";
                    String errorDesc = payment.has("error_description") ?
                            payment.get("error_description") : "Payment failed";
                    reason = String.format("Razorpay Error: %s - %s", errorCode, errorDesc);
                }
            } catch (Exception e) {
                log.error("Error fetching Razorpay payment details: {}", e.getMessage());
            }

            maintenancePaymentService.failPayment(paymentId,
                    reason != null ? reason : "Payment failed as reported by user");

            ApiResponse<String> response = new ApiResponse<>(
                    true,
                    "Payment marked as failed",
                    "Payment failure recorded successfully"
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error reporting payment failure: {}", e.getMessage(), e);
            ApiResponse<String> response = new ApiResponse<>(
                    false,
                    "Failed to report payment failure: " + e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Search maintenance payments with filters
     */
    @PostMapping("/search-list")
    public ResponseEntity<ApiResponse<List<MaintenancePayment>>> searchPayments(
            @RequestBody MaintenancePaymentFilter filter) {
        List<MaintenancePayment> payments = maintenancePaymentService.searchMaintenancePayment(filter);
        ApiResponse<List<MaintenancePayment>>response = new ApiResponse<>(true,"Payments fetched successfully.",payments);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<MaintenancePayment>>searchPaymentsPaginated(@RequestBody MaintenancePaymentFilter filter , @RequestParam(defaultValue = "0") Integer pageNumber,
@RequestParam(defaultValue = "6") Integer pageSize){
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Page<MaintenancePayment> payments = maintenancePaymentService.searchMaintenancPaymentPaginated(filter,pageable);
        return ResponseEntity.ok(payments);
    }
    /**
     * Get payment by ID
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<MaintenancePayment>> getPaymentById(@PathVariable Long paymentId) {
        MaintenancePayment payment = maintenancePaymentService.findPaymentById(paymentId);
        ApiResponse<MaintenancePayment>response = new ApiResponse<>(true,"Payment fetched successfully.",payment);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all payments for a specific flat
     */
    @GetMapping("/flat/{flatId}")
    public ResponseEntity<ApiResponse<List<MaintenancePayment>>> getPaymentsByFlat(@PathVariable Long flatId) {
        MaintenancePaymentFilter filter = new MaintenancePaymentFilter();
        filter.setFlat(flatId);
        List<MaintenancePayment> payments = maintenancePaymentService.searchMaintenancePayment(filter);
        ApiResponse<List<MaintenancePayment>>response = new ApiResponse<>(true,"Payments fetched successfully.",payments);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all payments for logged-in user
     */
    @PostMapping("/my-payments")
    public ResponseEntity<Page<MaintenancePayment>> getMyPayments( @RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "6") Integer pageSize ,@RequestBody MaintenancePaymentFilter filter) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        filter.setUser(user.getId());
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Page<MaintenancePayment> payments = maintenancePaymentService.searchMaintenancPaymentPaginated(filter,pageable);
        return ResponseEntity.ok(payments);
    }

    /**
     * Get active payment for a flat
     */
    @GetMapping("/flat/{flatId}/active")
    public ResponseEntity<ApiResponse<MaintenancePayment>> getActivePayment(@PathVariable Long flatId) {
        MaintenancePaymentFilter filter = new MaintenancePaymentFilter();
        filter.setFlat(flatId);
        filter.setStatus("COMPLETED");
        filter.setActivePaymentDate(LocalDate.now());
        List<MaintenancePayment> payments = maintenancePaymentService.searchMaintenancePayment(filter);

        if (!payments.isEmpty()) {
            ApiResponse<MaintenancePayment>response = new ApiResponse<>(true,"Payment fetched successfully.", payments.get(0));
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * Get payments by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<MaintenancePayment>>> getPaymentsByStatus(@PathVariable String status) {
        MaintenancePaymentFilter filter = new MaintenancePaymentFilter();
        filter.setStatus(status);
        List<MaintenancePayment> payments = maintenancePaymentService.searchMaintenancePayment(filter);
        ApiResponse<List<MaintenancePayment>>response = new ApiResponse<>(true,"Payments fetched successfully.",payments);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payments expiring soon (within next 7 days)
     */
    @GetMapping("/expiring-soon")
    public ResponseEntity<ApiResponse<List<MaintenancePayment>>> getExpiringSoon() {
        MaintenancePaymentFilter filter = new MaintenancePaymentFilter();
        filter.setActivePaymentDate(LocalDate.now());
        filter.setIsExpiringSoon(true);
        filter.setDaysUntilExpiry(7);
        List<MaintenancePayment> payments = maintenancePaymentService.searchMaintenancePayment(filter);
        ApiResponse<List<MaintenancePayment>>response = new ApiResponse<>(true,"Payments fetched successfully.",payments);
        return ResponseEntity.ok(response);
    }

    // ==================== SCHEDULER TEST ENDPOINTS ====================

    @PostMapping("/scheduler/trigger-monthly-reminders")
    public ResponseEntity<String> triggerMonthlyReminders() {
        paymentReminderScheduler.sendMonthlyPaymentReminders();
        return ResponseEntity.ok("Monthly payment reminders triggered successfully");
    }

    @PostMapping("/scheduler/trigger-expiring-reminders")
    public ResponseEntity<String> triggerExpiringReminders() {
        paymentReminderScheduler.sendExpiringPaymentReminders();
        return ResponseEntity.ok("Expiring payment reminders triggered successfully");
    }

}