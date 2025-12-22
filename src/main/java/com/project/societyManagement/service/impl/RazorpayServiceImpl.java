package com.project.societyManagement.service.impl;

import com.project.societyManagement.config.RazorpayConfig;
import com.project.societyManagement.dto.Razorpay.PaymentVerificationDTO;
import com.project.societyManagement.dto.Razorpay.RazorpayOrderDTO;
import com.project.societyManagement.entity.MaintenancePayment;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.service.MaintenancePaymentService;
import com.project.societyManagement.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RazorpayServiceImpl implements RazorpayService {

    private final RazorpayClient razorpayClient;
    private final RazorpayConfig razorpayConfig;
    private final MaintenancePaymentService maintenancePaymentService;

    @Override
    public RazorpayOrderDTO createOrder(Long paymentId, Double amount, String currency) throws Exception {
        try {
            // Get payment details
            MaintenancePayment payment = maintenancePaymentService.findPaymentById(paymentId);
            User user = payment.getUser();

            // Create Razorpay Order
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (amount * 100)); // Amount in paise
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", payment.getReferenceNumber());

            JSONObject notes = new JSONObject();
            notes.put("payment_id", paymentId);
            notes.put("flat_id", payment.getFlat().getId());
            notes.put("billing_cycle", payment.getBillingCycle().toString());
            orderRequest.put("notes", notes);

            Order order = razorpayClient.orders.create(orderRequest);

            // Build response DTO
            return RazorpayOrderDTO.builder()
                    .orderId(order.get("id"))
                    .currency(order.get("currency"))
                    .amount(amount)
                    .razorpayKeyId(razorpayConfig.getKeyId())
                    .paymentId(paymentId)
                    .customerName(user.getName())
                    .customerEmail(user.getEmail())
                    .customerContact(user.getPhoneNumber())
                    .build();

        } catch (Exception e) {
            log.error("Error creating Razorpay order: {}", e.getMessage());
            throw new Exception("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyPaymentSignature(PaymentVerificationDTO verificationDTO) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", verificationDTO.getRazorpayOrderId());
            options.put("razorpay_payment_id", verificationDTO.getRazorpayPaymentId());
            options.put("razorpay_signature", verificationDTO.getRazorpaySignature());

            return Utils.verifyPaymentSignature(options, razorpayConfig.getKeySecret());
        } catch (Exception e) {
            log.error("Error verifying payment signature: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signature) {
        // Not needed for non-webhook implementation
        return false;
    }

    @Override
    public Payment getPaymentDetails(String razorpayPaymentId) throws Exception {
        try {
            return razorpayClient.payments.fetch(razorpayPaymentId);
        } catch (Exception e) {
            log.error("Error fetching payment details: {}", e.getMessage());
            throw new Exception("Failed to fetch payment details: " + e.getMessage());
        }
    }

    @Override
    public Order getOrderDetails(String razorpayOrderId) throws Exception {
        try {
            return razorpayClient.orders.fetch(razorpayOrderId);
        } catch (Exception e) {
            log.error("Error fetching order details: {}", e.getMessage());
            throw new Exception("Failed to fetch order details: " + e.getMessage());
        }
    }
}