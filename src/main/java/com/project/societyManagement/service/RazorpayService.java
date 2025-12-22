package com.project.societyManagement.service;

import com.project.societyManagement.dto.Razorpay.PaymentVerificationDTO;
import com.project.societyManagement.dto.Razorpay.RazorpayOrderDTO;
import com.razorpay.Order;
import com.razorpay.Payment;

public interface RazorpayService {
    public RazorpayOrderDTO createOrder(Long paymentId, Double amount, String currency) throws Exception ;
    public boolean verifyPaymentSignature(PaymentVerificationDTO verificationDTO) ;
    public boolean verifyWebhookSignature(String payload, String signature) ;
    public Payment getPaymentDetails(String razorpayPaymentId) throws Exception;
    public Order getOrderDetails(String razorpayOrderId) throws Exception ;
}
