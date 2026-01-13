package com.project.societyManagement.service.impl;

import com.project.societyManagement.dto.MaintenancePayment.PaymentReceiptDTO;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

@Service
public class EmailTemplateService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public String generatePaymentReceiptHtml(PaymentReceiptDTO receipt) {
        return "<!DOCTYPE html>" +
                "<html xmlns='http://www.w3.org/1999/xhtml'>" +
                "<head>" +
                "<meta charset='UTF-8' />" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0' />" +
                "<style type='text/css'>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; margin: 0; padding: 0; }" +
                ".container { max-width: 650px; margin: 20px auto; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }" +
                ".header h1 { margin: 0; font-size: 28px; }" +
                ".header p { margin: 5px 0 0 0; font-size: 14px; opacity: 0.9; }" +
                ".content { padding: 30px; }" +
                ".success-badge { background: #10b981; color: white; padding: 8px 20px; border-radius: 20px; display: inline-block; font-weight: bold; margin-bottom: 20px; }" +
                ".info-section { margin-bottom: 25px; }" +
                ".info-section h2 { color: #667eea; font-size: 18px; margin-bottom: 15px; border-bottom: 2px solid #667eea; padding-bottom: 5px; }" +
                ".info-row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #e5e7eb; width:100%; }" +
                ".info-row:last-child { border-bottom: none; }" +
                ".info-label { font-weight: 600; color: #6b7280; flex-shrink: 0; }" +
                ".info-value { color: #111827; text-align: right; word-break: break-word; }" +
                ".amount-section { background: #f9fafb; padding: 20px; border-radius: 8px; margin: 20px 0; }" +
                ".total-amount { display: flex; justify-content: space-between; font-size: 20px; font-weight: bold; color: #667eea; padding-top: 15px; border-top: 2px solid #667eea; }" +
                ".warning-box { background: #fef3c7; border-left: 4px solid #f59e0b; padding: 15px; margin: 20px 0; border-radius: 4px; }" +
                ".footer { background: #f9fafb; padding: 20px 30px; text-align: center; font-size: 12px; color: #6b7280; }" +
                ".footer p { margin: 5px 0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +

                "<div class='header'>" +
                "<h1>Payment Receipt</h1>" +
                "<p>" + (receipt.getSocietyName() != null ? receipt.getSocietyName() : "Society Management System") + "</p>" +
                "</div>" +

                "<div class='content'>" +
                "<div class='success-badge'>✓ Payment Successful</div>" +

                "<p>Dear " + receipt.getUserName() + ",</p>" +
                "<p>Thank you for your payment. Your maintenance fee has been successfully processed.</p>" +

                "<p style='margin-top: 20px;'>Best regards,<br />" +
                "<strong>Society Management Team</strong></p>" +
                "</div>" +

                "<div class='footer'>" +
                "<p>This is an automated receipt. Please do not reply to this email.</p>" +
                "<p>© 2025 Society Management System. All rights reserved.</p>" +
                "</div>" +

                "</div>" +
                "</body>" +
                "</html>";
    }

}