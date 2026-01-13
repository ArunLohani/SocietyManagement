package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.ImpersonationSession.CurrentImpersonationDto;
import com.project.societyManagement.dto.ImpersonationSession.StartImpersonationResponseDto;
import com.project.societyManagement.dto.ImpersonationSession.ValidateImpersonationDto;
import com.project.societyManagement.entity.ImpersonationSession;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.queryBuilder.impersonationSession.ImpersonationSessionFilter;
import com.project.societyManagement.config.ImpersonationAuthenticationToken;
import com.project.societyManagement.service.ImpersonationSessionService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/impersonation")
@RequiredArgsConstructor
@Slf4j
public class ImpersonationSessionController {

    private final ImpersonationSessionService impersonationSessionService;

    /**
     * Start impersonation session for a support ticket
     * Accessible by: SUPER_ADMIN
     */
    @PostMapping("/start/{ticketId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<StartImpersonationResponseDto>> startImpersonation(
            @PathVariable Long ticketId,
            HttpServletResponse response,
            @AuthenticationPrincipal User superAdmin) {

        ImpersonationSession session =
                impersonationSessionService.createImpersonationSession(ticketId, response);

        StartImpersonationResponseDto dto = new StartImpersonationResponseDto(
                session.getId(),
                session.getAdmin().getEmail(),
                session.getExpiresAt(),
                session.getTicket().getId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Impersonation session started successfully",
                        dto
                ));
    }


    /**
     * End impersonation session
     * Accessible by: ADMIN, SUPER_ADMIN
     */
    @PostMapping("/end/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> endImpersonation(
            @PathVariable Long sessionId,
            HttpServletResponse response,
            @AuthenticationPrincipal User user) {

        log.info("User {} ending impersonation session {}", user.getId(), sessionId);

        impersonationSessionService.endImpersonationSession(sessionId, user);

        ResponseCookie clearCookie = ResponseCookie.from("impersonation_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Impersonation session ended successfully",
                            sessionId.toString()
                )
        );
    }

    /**
     * Get current impersonation session details
     */
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<CurrentImpersonationDto>> getCurrentImpersonationSession() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof ImpersonationAuthenticationToken token)
                || !token.isImpersonating()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Not in an impersonation session", null));
        }

        ImpersonationSession session =
                impersonationSessionService.findSessionById(token.getSessionId());

        CurrentImpersonationDto dto = new CurrentImpersonationDto(
                session.getId(),
                true,
                token.getSuperAdminEmail(),
                session.getAdmin().getEmail(),
                session.getExpiresAt(),
                session.getTicket().getId()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Current impersonation session fetched",
                        dto
                )
        );
    }

    /**
     * Get impersonation session by ID
     * Accessible by: ADMIN (if involved), SUPER_ADMIN
     */
    @GetMapping("/sessions/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ImpersonationSession>> getSessionById(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal User user) {

        log.info("User {} fetching impersonation session {}", user.getId(), sessionId);

        ImpersonationSession session =
                impersonationSessionService.findSessionById(sessionId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Impersonation session fetched successfully",
                        session
                )
        );
    }

    /**
     * Get all impersonation sessions with filters
     */
    @PostMapping("/sessions")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<ImpersonationSession>>> getSessions(
            @RequestBody ImpersonationSessionFilter filter) {

        List<ImpersonationSession> sessions =
                impersonationSessionService.getSessions(filter);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Impersonation sessions fetched successfully",
                        sessions
                )
        );
    }

    @PostMapping("/sessions/paginated")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Page<ImpersonationSession>> getSessionsPaginated(
            @RequestBody ImpersonationSessionFilter filter,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Page<ImpersonationSession> sessions =
                impersonationSessionService.getSessionsPaginated(filter,pageable);

        return ResponseEntity.ok(sessions);

    }

    /**
     * Get active impersonation sessions for a ticket
     */
    @GetMapping("/sessions/ticket/{ticketId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<ImpersonationSession>>> getActiveSessionsForTicket(
            @PathVariable Long ticketId) {

        ImpersonationSessionFilter filter = new ImpersonationSessionFilter();
        filter.setTicket(ticketId);
        filter.setExpiresAtFrom(LocalDateTime.now());
        filter.setIsActive(true);

        List<ImpersonationSession> sessions =
                impersonationSessionService.getSessions(filter);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Active impersonation sessions fetched",
                        sessions
                )
        );
    }

    /**
     * Check if impersonation session is active
     */
    @GetMapping("/sessions/{sessionId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> checkSessionActive(
            @PathVariable Long sessionId) {

        boolean isActive =
                impersonationSessionService.isSessionActive(sessionId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Session active status fetched",
                        isActive
                )
        );
    }


    /**
     * Validate impersonation token (Frontend helper)
     */
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<ValidateImpersonationDto>> validateImpersonation() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ValidateImpersonationDto dto = new ValidateImpersonationDto();

        if (auth instanceof ImpersonationAuthenticationToken token
                && token.isImpersonating()) {

            boolean isActive =
                    impersonationSessionService.isSessionActive(token.getSessionId());

            dto.setImpersonating(true);
            dto.setSessionId(token.getSessionId());
            dto.setSuperAdminEmail(token.getSuperAdminEmail());
            dto.setIsActive(isActive);

            if (!isActive) {
                dto.setMessage("Impersonation session expired or terminated");
            }
        } else {
            dto.setImpersonating(false);
        }

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Impersonation validation completed",
                        dto
                )
        );
    }

}
