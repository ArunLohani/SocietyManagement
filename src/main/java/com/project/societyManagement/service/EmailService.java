package com.project.societyManagement.service;

public interface EmailService {
    public void sendSimpleEmail(String to, String subject, String text);
   public void sendHtmlEmail(String to, String subject, String htmlContent);
}
