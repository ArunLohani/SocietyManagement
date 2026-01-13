package com.project.societyManagement.queryBuilder.impersonationSession;

import com.project.societyManagement.entity.SupportTicket;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.SortFilter;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImpersonationSessionFilter {

    private Long id;
    private Long admin;
    private Long superAdmin;
    private Long ticket;
    private LocalDateTime expiresAtFrom;
    private LocalDateTime expiresAtTo;
    private LocalDateTime endedAtFrom;
    private LocalDateTime endedAtTo;
    private Boolean endedAtIsNull; // NEW: Check if endedAt is null
    private Boolean isActive = true;
    private SortFilter sortFilter = new SortFilter("createdAt", false);

}