package com.project.societyManagement.dto.SupportTicket;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRaiseRequest {
    @NotEmpty(message = "Ticket Title cannot be empty.")
    private String title;
    @NotEmpty(message = "Ticket Description cannot be empty.")
    private String description;
}
