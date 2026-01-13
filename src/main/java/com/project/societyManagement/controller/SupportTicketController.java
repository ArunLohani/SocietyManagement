package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.SupportTicket.TicketRaiseRequest;
import com.project.societyManagement.entity.SupportTicket;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.queryBuilder.supportTicket.SupportTicketFilter;
import com.project.societyManagement.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/support-tickets")
@RequiredArgsConstructor
@Slf4j
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    /**
     * Create a new support ticket
     * Accessible by: ADMIN
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SupportTicket>> raiseTicket(
             @RequestBody TicketRaiseRequest request,
            @AuthenticationPrincipal User user) {

        log.info("User {} raising support ticket: {}", user.getId(), request.getTitle());

        SupportTicket ticket = supportTicketService.raiseSupportTicket(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Support ticket created successfully",
                        ticket
                ));
    }

    /**
     * Get ticket by ID
     * Accessible by: ADMIN, SUPER_ADMIN
     */
    @GetMapping("/{ticketId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SupportTicket>> getTicketById(
            @PathVariable Long ticketId) {

        SupportTicket ticket = supportTicketService.getTicketById(ticketId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Support ticket fetched successfully",
                        ticket
                )
        );
    }

    /**
     * Get all tickets with filters
     */
    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<SupportTicket>>> getTickets(
            @RequestBody SupportTicketFilter filter) {

        List<SupportTicket> tickets = supportTicketService.getSupportTicket(filter);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Support tickets fetched successfully",
                        tickets
                )
        );
    }

    /**
     * Get paginated tickets
     */
    @PostMapping("/search/paginated")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Page<SupportTicket>> getTicketsPaginated(
            @RequestBody SupportTicketFilter filter,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<SupportTicket> tickets =
                supportTicketService.getSupportTicketPaginated(filter, pageable);

        return ResponseEntity.ok(tickets
        );
    }

    /**
     * Update ticket status
     * Accessible by: SUPER_ADMIN
     */
    @PatchMapping("/{ticketId}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SupportTicket>> updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestParam String status,
            @AuthenticationPrincipal User user) {

        SupportTicket ticket =
                supportTicketService.changeSupportTicketStatus(ticketId, status);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Ticket status updated successfully",
                        ticket
                )
        );
    }

    /**
     * Update impersonation settings
     */
    @PatchMapping("/{ticketId}/impersonation")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SupportTicket>> updateImpersonationSettings(
            @PathVariable Long ticketId,
            @RequestParam(required = false) Boolean allowImpersonation,
            @RequestParam(required = false) LocalDateTime impersonationUntil,
            @AuthenticationPrincipal User user) {

        SupportTicket ticket = supportTicketService.getTicketById(ticketId);

        // Ownership check for ADMIN
        if (user.getRoles().stream().anyMatch(r -> "ADMIN".equals(r.getRole()))
                && !ticket.getRaisedBy().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(
                            false,
                            "You are not allowed to modify this ticket",
                            null
                    ));
        }

        if (allowImpersonation != null) {
            ticket.setAllowImpersonation(allowImpersonation);
        }
        if (impersonationUntil != null) {
            ticket.setImpersonationUntil(impersonationUntil);
        }

        SupportTicket updated =
                supportTicketService.changeSupportTicketStatus(ticketId, ticket.getStatus().name());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Impersonation settings updated successfully",
                        updated
                )
        );
    }




}
