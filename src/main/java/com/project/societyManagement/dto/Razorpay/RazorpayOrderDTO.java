package com.project.societyManagement.dto.Razorpay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RazorpayOrderDTO {
    private String orderId;
    private String currency;
    private Double amount;
    private String razorpayKeyId;
    private Long paymentId; // Your internal payment ID
    private String customerName;
    private String customerEmail;
    private String customerContact;
}
