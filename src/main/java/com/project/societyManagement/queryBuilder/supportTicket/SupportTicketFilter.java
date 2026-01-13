package com.project.societyManagement.queryBuilder.supportTicket;

import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.SortFilter;
import com.project.societyManagement.entity.types.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketFilter {
    private Long id;
    private String title;
    private String description;
    private Long raisedBy;
    private String status;
    private Boolean allowImpersonation;
    private LocalDateTime impersonationUntil;
    private Boolean isActive = true;
    private SortFilter sortFilter = new SortFilter("createdAt", false);
}
