package com.project.societyManagement.service.impl;

import com.project.societyManagement.exception.UserNotFoundException;
import com.project.societyManagement.queryBuilder.impersonationSession.ImpersonationSessionFilter;
import com.project.societyManagement.queryBuilder.impersonationSession.ImpersonationSessionQueryBuilder;
import com.project.societyManagement.repository.ImpersonationSessionRepo;
import com.project.societyManagement.service.ImpersonationSessionService;
import com.project.societyManagement.service.SupportTicketService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.project.societyManagement.entity.ImpersonationSession;
import com.project.societyManagement.entity.SupportTicket;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.TicketStatus;
import com.project.societyManagement.util.AuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImpersonationSessionServiceImpl implements ImpersonationSessionService {

    private final ImpersonationSessionQueryBuilder impersonationSessionQueryBuilder;
    private final ImpersonationSessionRepo impersonationSessionRepository;
    private final SupportTicketService supportTicketService;
    private final AuthUtil authUtil;

    public ImpersonationSession findSessionById(Long sessionId){
        ImpersonationSessionFilter filter = new ImpersonationSessionFilter();
        filter.setId(sessionId);
        ImpersonationSession session = impersonationSessionQueryBuilder.findById(filter);
        return session;
    }

    /**
     * Creates an impersonation session for super admin to access admin's account
     */
    @Transactional
    public ImpersonationSession createImpersonationSession(Long ticketId, HttpServletResponse response) {
        SupportTicket ticket = supportTicketService.getTicketById(ticketId);
        User superAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Validate ticket status and permissions
        validateImpersonationRequest(ticket, superAdmin);

        // Calculate expiration time
        LocalDateTime expiresAt = ticket.getImpersonationUntil() != null
                ? ticket.getImpersonationUntil()
                : LocalDateTime.now().plusHours(24);

        // Create impersonation session
        ImpersonationSession session = ImpersonationSession.builder()
                .ticket(ticket)
                .admin(ticket.getRaisedBy())
                .superAdmin(superAdmin)
                .expiresAt(expiresAt)
                .build();

        ImpersonationSession savedSession = impersonationSessionRepository.save(session);
        String impersonationToken = generateImpersonationToken(savedSession);
        ResponseCookie jwtCookie = ResponseCookie.from("impersonation_token", impersonationToken)
                .httpOnly(true)              // IMPORTANT for security
                .secure(true)                // REQUIRED for HTTPS
                .sameSite("None")            // REQUIRED for cross-site
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        log.info("Impersonation session created: SuperAdmin {} impersonating Admin {} for ticket {}",
                superAdmin.getId(), ticket.getRaisedBy().getId(), ticketId);

        return savedSession;
    }

    /**
     * Generates an impersonation JWT token
     */
    public String generateImpersonationToken(ImpersonationSession session) {
        User admin = session.getAdmin();
        return authUtil.getImpersonationToken(admin, session.getSuperAdmin(), session.getId());
    }

    /**
     * Validates if impersonation is allowed
     */
    private void validateImpersonationRequest(SupportTicket ticket, User superAdmin) {
        if (!ticket.getAllowImpersonation()) {
            throw new IllegalStateException("Impersonation not allowed for this ticket");
        }

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new IllegalStateException("Cannot impersonate for closed or resolved tickets");
        }

        if (ticket.getImpersonationUntil() != null &&
                ticket.getImpersonationUntil().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Impersonation period has expired");
        }

        if (!superAdmin.getRoles().stream()
                .anyMatch(role -> "SUPER_ADMIN".equals(role.getRole()))) {
            throw new SecurityException("User does not have super admin privileges");
        }

        // Check for truly active sessions:
        // - endedAt IS NULL (not manually terminated)
        // - expiresAt > NOW (not expired)
        // - isActive = true (soft delete)
        ImpersonationSessionFilter filter = new ImpersonationSessionFilter();
        filter.setTicket(ticket.getId());
        filter.setEndedAtIsNull(true); // NEW: endedAt must be null
        filter.setExpiresAtFrom(LocalDateTime.now()); // expiresAt must be in the future
        filter.setIsActive(true); // must be active

        List<ImpersonationSession> activeSessions = impersonationSessionQueryBuilder.search(filter);

        if (!activeSessions.isEmpty()) {
            throw new IllegalStateException("An active impersonation session already exists for this ticket");
        }
    }

    /**
     * Ends an impersonation session
     */
    @Transactional
    public void endImpersonationSession(Long sessionId, User user) {

        ImpersonationSession session = impersonationSessionRepository.findByIdWithRelations(sessionId).orElseThrow(() -> new RuntimeException("No Impersonation Session Found"));

        // Only super admin or the admin being impersonated can end the session
        if (!session.getSuperAdmin().getId().equals(user.getId()) &&
                !session.getAdmin().getId().equals(user.getId())) {
            throw new SecurityException("Unauthorized to end this session");
        }

        session.setEndedAt(LocalDateTime.now());
        impersonationSessionRepository.save(session);

        log.info("Impersonation session {} ended by user {}", sessionId, user.getId());
    }

    public boolean isSessionActiveWithoutAuth(Long sessionId) {
        return impersonationSessionRepository.isSessionActive(
                sessionId,
                LocalDateTime.now()
        );
    }

    /**
     * Validates if a session is still active
     */
    public boolean isSessionActive(Long sessionId) {
        ImpersonationSessionFilter filter = new ImpersonationSessionFilter();
        filter.setId(sessionId);
        filter.setExpiresAtFrom(LocalDateTime.now());
        try{
            ImpersonationSession activeSession = impersonationSessionQueryBuilder.findById(filter);
            return true;
        } catch (UserNotFoundException e) {
            return false;
        }
    }
    public List<ImpersonationSession> getSessions(ImpersonationSessionFilter filter) {
        return impersonationSessionQueryBuilder.search(filter);
    }

    public Page<ImpersonationSession> getSessionsPaginated(
            ImpersonationSessionFilter filter, Pageable pageable) {
        return impersonationSessionQueryBuilder.searchPaginated(filter, pageable);
    }
}