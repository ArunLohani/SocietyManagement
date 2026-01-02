package com.project.societyManagement.entity.types;

public enum VisitorStatus {
    PENDING,     // Request created, waiting for resident action
    APPROVED,    // Approved by resident, OTP generated
    REJECTED,    // Rejected by resident
    ENTERED,     // OTP verified, visitor entered
    EXITED,      // Visitor exited society
    EXPIRED,      // OTP expired / visit window missed,
    CANCELLED
}
