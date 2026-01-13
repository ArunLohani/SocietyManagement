package com.project.societyManagement.service.impl;

import com.project.societyManagement.dto.Visitor.GuestRequestDTO;
import com.project.societyManagement.dto.Visitor.VisitorResponseDTO;
import com.project.societyManagement.entity.Flat;
import com.project.societyManagement.entity.FlatMember;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.VisitorRequest;
import com.project.societyManagement.entity.types.VisitorStatus;
import com.project.societyManagement.entity.types.VisitorType;
import com.project.societyManagement.queryBuilder.flatMembers.FlatMembersFilter;
import com.project.societyManagement.queryBuilder.visitorRequest.VisitorRequestFilter;
import com.project.societyManagement.queryBuilder.visitorRequest.VisitorRequestQueryBuilder;
import com.project.societyManagement.repository.VisitorRequestRepo;
import com.project.societyManagement.service.*;
import com.project.societyManagement.util.OtpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisitorRequestServiceImpl implements VisitorRequestService {

    private final VisitorRequestQueryBuilder visitorRequestQueryBuilder;
    private final VisitorRequestRepo visitorRequestRepo;
    private final FlatService flatService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final OtpUtil otpService;
    private final EmailService emailService;
    private final FlatMembersService flatMembersService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    /**
     * Find visitor request by ID
     */
    public VisitorRequest findVisitorRequestById(Long id) {
        VisitorRequestFilter filter = new VisitorRequestFilter();
        filter.setId(id);
        return visitorRequestQueryBuilder.findById(filter);
    }

    /**
     * Get all visitor requests with filters and pagination
     */
    public Page<VisitorResponseDTO> getAllVisitorRequests(VisitorRequestFilter filter, Pageable pageable) {
        Page<VisitorRequest> requests = visitorRequestQueryBuilder.searchPaginated(filter, pageable);
        return requests.map(request -> modelMapper.map(request, VisitorResponseDTO.class));
    }

    /**
     * Get visitor requests without pagination
     */
    public List<VisitorResponseDTO> getAllVisitorRequests(VisitorRequestFilter filter) {
        List<VisitorRequest> requests = visitorRequestQueryBuilder.search(filter);
        return requests.stream()
                .map(request -> modelMapper.map(request, VisitorResponseDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get single visitor request details by ID
     */
    public VisitorResponseDTO getVisitorRequestById(Long id) {
        VisitorRequest request = findVisitorRequestById(id);
        return modelMapper.map(request, VisitorResponseDTO.class);
    }

    // ==================== Resident Operations ====================

    /**
     * Create visitor request by flat member/resident (Pre-approved with OTP)
     */
    @Transactional
    public VisitorResponseDTO createVisitorRequestByResident(GuestRequestDTO visitorRequest) {
        Flat flat = flatService.getFlatById(visitorRequest.getFlat());
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        VisitorRequest request = modelMapper.map(visitorRequest, VisitorRequest.class);
        String otp = otpService.generateOtp();

        // Set type as GUEST for resident-created requests
        request.setType(VisitorType.GUEST);
        request.setFlat(flat);
        request.setRequestedBy(user);
        request.setOtp(otpService.hashOtp(otp));
        request.setOtpGeneratedAt(LocalDateTime.now());
        request.setOtpExpiresAt(LocalDateTime.now().plusHours(2));
        request.setStatus(VisitorStatus.APPROVED);
        request.setApprovedAt(LocalDateTime.now());

        request = visitorRequestRepo.save(request);
        sendVisitorOtpEmail(request, otp, flat, user);

        log.info("Guest visitor request created and approved by resident: {} for visitor: {}",
                user.getId(), request.getVisitorName());

        return modelMapper.map(request, VisitorResponseDTO.class);
    }

    /**
     * Update visitor request details (before approval)
     */
    @Transactional
    public VisitorResponseDTO updateVisitorRequest(Long id, GuestRequestDTO visitorRequestDTO) {
        VisitorRequest request = findVisitorRequestById(id);

        // Only allow updates if not yet entered or exited
        if (request.getStatus() == VisitorStatus.ENTERED || request.getStatus() == VisitorStatus.EXITED) {
            throw new RuntimeException("Cannot update visitor request after entry");
        }

        // Update allowed fields
        if (visitorRequestDTO.getVisitorName() != null) {
            request.setVisitorName(visitorRequestDTO.getVisitorName());
        }
        if (visitorRequestDTO.getVisitorPhone() != null) {
            request.setVisitorPhone(visitorRequestDTO.getVisitorPhone());
        }
        if (visitorRequestDTO.getVisitorEmail() != null) {
            request.setVisitorEmail(visitorRequestDTO.getVisitorEmail());
        }
        if (visitorRequestDTO.getPurpose() != null) {
            request.setPurpose(visitorRequestDTO.getPurpose());
        }
        if (visitorRequestDTO.getExpectedIn() != null) {
            request.setExpectedIn(visitorRequestDTO.getExpectedIn());
        }
        if (visitorRequestDTO.getExpectedOut() != null) {
            request.setExpectedOut(visitorRequestDTO.getExpectedOut());
        }

        request = visitorRequestRepo.save(request);
        log.info("Visitor request updated: {}", id);

        return modelMapper.map(request, VisitorResponseDTO.class);
    }

    /**
     * Cancel visitor request (soft delete)
     */
    @Transactional
    public VisitorResponseDTO cancelVisitorRequest(Long id) {
        VisitorRequest request = findVisitorRequestById(id);

        if (request.getStatus() == VisitorStatus.ENTERED) {
            throw new RuntimeException("Cannot cancel visitor request after entry");
        }

        request.setStatus(VisitorStatus.CANCELLED);
        request.setIsActive(false);
        request.setOtp(null); // Invalidate OTP

        request = visitorRequestRepo.save(request);
        log.info("Visitor request cancelled: {}", id);

        return modelMapper.map(request, VisitorResponseDTO.class);
    }

    // ==================== Guard/Security Operations ====================

    /**
     * Create walk-in visitor request by guard (Pending approval, NO OTP)
     */
    @Transactional
    public VisitorResponseDTO createWalkInVisitorRequest(GuestRequestDTO visitorRequest) {
        Flat flat = flatService.getFlatById(visitorRequest.getFlat());
        User guard = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        VisitorRequest request = modelMapper.map(visitorRequest, VisitorRequest.class);
        request.setType(VisitorType.WALKIN);
        request.setFlat(flat);
        request.setRequestedBy(guard);
        request.setStatus(VisitorStatus.PENDING);
        // NO OTP for walk-in visitors
        request.setOtp(null);
        request.setOtpGeneratedAt(null);
        request.setOtpExpiresAt(null);

        request = visitorRequestRepo.save(request);

        // Notify flat residents about pending visitor
        notifyResidentAboutPendingVisitor(request, flat);

        log.info("Walk-in visitor request created by guard: {} for flat: {}",
                guard.getId(), flat.getId());

        return modelMapper.map(request, VisitorResponseDTO.class);
    }

    /**
     * Verify OTP at entry gate (ONLY for GUEST type visitors)
     */
    @Transactional
    public VisitorResponseDTO verifyOtpAndMarkEntry(Long id, String otp) {
        VisitorRequest request = findVisitorRequestById(id);

        // Security check: Only GUEST visitors can use OTP
        if (request.getType() != VisitorType.GUEST) {
            throw new RuntimeException("OTP verification is only allowed for pre-approved guest visitors. Walk-in visitors don't require OTP.");
        }

        if (request.getStatus() != VisitorStatus.APPROVED) {
            throw new RuntimeException("Visitor request is not approved");
        }

        if (request.getOtp() == null) {
            throw new RuntimeException("No OTP found for this visitor");
        }

        if (request.getOtpExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (!otpService.matches(otp, request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        request.setStatus(VisitorStatus.ENTERED);
        request.setEnteredAt(LocalDateTime.now());
        request.setOtp(null); // Prevent reuse

        request = visitorRequestRepo.save(request);
        log.info("Guest visitor entered with OTP verification: {}", id);

        return modelMapper.map(request, VisitorResponseDTO.class);
    }


    /**
     * Mark visitor entry for WalkIn Guest
     */
    public VisitorResponseDTO markVisitorEntry(Long id) {
        VisitorRequest request = findVisitorRequestById(id);

        // Security check: Only GUEST visitors can use OTP
        if (request.getType() != VisitorType.WALKIN) {
            throw new RuntimeException("Only For WalkIn Guests.");
        }

        if (request.getStatus() != VisitorStatus.APPROVED) {
            throw new RuntimeException("Visitor request is not approved");
        }



        request.setStatus(VisitorStatus.ENTERED);
        request.setEnteredAt(LocalDateTime.now());
        request.setOtp(null); // Prevent reuse
        request = visitorRequestRepo.save(request);

        return modelMapper.map(request, VisitorResponseDTO.class);
    }
    /**
     * Mark visitor exit
     */
    @Transactional
    public VisitorResponseDTO markVisitorExit(Long id) {
        VisitorRequest request = findVisitorRequestById(id);

        if (request.getStatus() != VisitorStatus.ENTERED) {
            throw new RuntimeException("Visitor has not entered yet");
        }

        request.setStatus(VisitorStatus.EXITED);
        request.setExitedAt(LocalDateTime.now());

        request = visitorRequestRepo.save(request);
        log.info("Visitor exited: {}", id);

        return modelMapper.map(request, VisitorResponseDTO.class);
    }

    /**
     * Reject visitor entry at gate
     */
    @Transactional
    public VisitorResponseDTO rejectVisitorEntry(Long id, String reason) {
        VisitorRequest request = findVisitorRequestById(id);

        request.setStatus(VisitorStatus.REJECTED);
        request.setOtp(null); // Invalidate OTP if exists
        request.setRejectionReason(reason);

        request = visitorRequestRepo.save(request);
        log.info("Visitor entry rejected: {} - Reason: {}", id, reason);

        return modelMapper.map(request, VisitorResponseDTO.class);
    }

    // ==================== Resident Approval Operations ====================

    /**
     * Approve pending walk-in visitor request (NO OTP - direct entry allowed)
     */
    @Transactional
    public VisitorResponseDTO approveVisitorRequest(Long id) {
        VisitorRequest request = findVisitorRequestById(id);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (request.getStatus() != VisitorStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be approved");
        }

        // Security check: Only WALKIN visitors should be in PENDING state
        if (request.getType() != VisitorType.WALKIN) {
            throw new RuntimeException("Only walk-in visitor requests can be approved through this endpoint");
        }

        // Walk-in visitors are approved directly - NO OTP needed
        request.setStatus(VisitorStatus.APPROVED);
        request.setApprovedAt(LocalDateTime.now());

        // NO OTP for walk-in visitors
        request.setOtp(null);
        request.setOtpGeneratedAt(null);
        request.setOtpExpiresAt(null);

        request = visitorRequestRepo.save(request);

        // Send approval notification to visitor (without OTP)
        sendWalkInApprovalEmail(request, request.getFlat(), user);

        log.info("Walk-in visitor request approved by resident: {} for request: {}", user.getId(), id);

        return modelMapper.map(request, VisitorResponseDTO.class);
    }

    /**
     * Reject pending walk-in visitor request
     */
    @Transactional
    public VisitorResponseDTO rejectVisitorRequest(Long id, String reason) {
        VisitorRequest request = findVisitorRequestById(id);

        if (request.getStatus() != VisitorStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be rejected");
        }

        // Security check: Only WALKIN visitors should be in PENDING state
        if (request.getType() != VisitorType.WALKIN) {
            throw new RuntimeException("Only walk-in visitor requests can be rejected through this endpoint");
        }

        request.setStatus(VisitorStatus.REJECTED);
        request.setRejectionReason(reason);

        request = visitorRequestRepo.save(request);

        // Optionally notify guard about rejection
        log.info("Walk-in visitor request rejected by resident for request: {} - Reason: {}", id, reason);

        return modelMapper.map(request, VisitorResponseDTO.class);
    }

    // ==================== OTP Management ====================

    /**
     * Regenerate OTP for approved visitor (ONLY for GUEST type)
     */
    @Transactional
    public VisitorResponseDTO regenerateOtp(Long id) {
        VisitorRequest request = findVisitorRequestById(id);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Security check: Only GUEST visitors can regenerate OTP
        if (request.getType() != VisitorType.GUEST) {
            throw new RuntimeException("OTP can only be regenerated for pre-approved guest visitors. Walk-in visitors don't use OTP.");
        }

        if (request.getStatus() != VisitorStatus.APPROVED) {
            throw new RuntimeException("Can only regenerate OTP for approved visitors");
        }

        String otp = otpService.generateOtp();
        request.setOtp(otpService.hashOtp(otp));
        request.setOtpGeneratedAt(LocalDateTime.now());
        request.setOtpExpiresAt(LocalDateTime.now().plusHours(2));

        request = visitorRequestRepo.save(request);
        sendVisitorOtpEmail(request, otp, request.getFlat(), user);

        log.info("OTP regenerated for visitor request: {}", id);

        return modelMapper.map(request, VisitorResponseDTO.class);
    }

    /**
     * Get currently present visitors in society
     */
    public List<VisitorResponseDTO> getCurrentVisitors() {
        VisitorRequestFilter filter = new VisitorRequestFilter();
        filter.setStatus(VisitorStatus.ENTERED.name());

        List<VisitorRequest> requests = visitorRequestQueryBuilder.search(filter);
        return requests.stream()
                .map(request -> modelMapper.map(request, VisitorResponseDTO.class))
                .collect(Collectors.toList());
    }

    // ==================== Helper/Private Methods ====================

    /**
     * Send OTP email to GUEST visitor (HTML formatted)
     */
    private void sendVisitorOtpEmail(VisitorRequest request, String otp, Flat flat, User resident) {
        String subject = "✅ Visitor Entry Approved – OTP for Society Entry";

        String validTill = request.getOtpExpiresAt().format(DATE_FORMATTER);

        String htmlContent = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
                    .content { background-color: #f9f9f9; padding: 30px; border: 1px solid #ddd; }
                    .otp-box { background-color: #fff; border: 2px dashed #4CAF50; padding: 20px; text-align: center; margin: 20px 0; border-radius: 5px; }
                    .otp { font-size: 32px; font-weight: bold; color: #4CAF50; letter-spacing: 5px; }
                    .details { background-color: #fff; padding: 15px; margin: 15px 0; border-left: 4px solid #4CAF50; }
                    .detail-row { padding: 5px 0; }
                    .label { font-weight: bold; color: #555; }
                    .instructions { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 15px 0; }
                    .footer { text-align: center; padding: 20px; color: #777; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🏢 Visitor Entry Approved</h1>
                    </div>
                    <div class="content">
                        <p>Dear <strong>%s</strong>,</p>
                        <p>Your visit request has been approved by the resident.</p>
                        
                        <div class="otp-box">
                            <p style="margin: 0; color: #666;">Your One-Time Password (OTP)</p>
                            <div class="otp">%s</div>
                            <p style="margin: 5px 0 0 0; color: #999; font-size: 14px;">Valid until: %s</p>
                        </div>
                        
                        <div class="details">
                            <h3 style="margin-top: 0; color: #4CAF50;">Visit Details</h3>
                            <div class="detail-row"><span class="label">🏠 Flat:</span> Block %s - %s</div>
                            <div class="detail-row"><span class="label">👤 Resident:</span> %s</div>
                            <div class="detail-row"><span class="label">📝 Purpose:</span> %s</div>
                        </div>
                        
                        <div class="instructions">
                            <h3 style="margin-top: 0;">⚠️ Important Instructions</h3>
                            <ul style="margin: 10px 0; padding-left: 20px;">
                                <li>Present this OTP to the security personnel at the gate</li>
                                <li>This OTP is valid for a limited time and can be used <strong>only once</strong></li>
                                <li><strong>Do not share</strong> this OTP with anyone else</li>
                                <li>Keep this email handy when you arrive at the society gate</li>
                            </ul>
                        </div>
                    </div>
                    <div class="footer">
                        <p>This is an automated message from the Society Management System.</p>
                        <p>For any queries, please contact the society administration.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                request.getVisitorName(),
                otp,
                validTill,
                flat.getBlock(),
                flat.getNumber(),
                resident.getName(),
                request.getPurpose() != null ? request.getPurpose() : "Not specified"
        );

        emailService.sendHtmlEmail(request.getVisitorEmail(), subject, htmlContent);
        log.info("OTP email sent to guest visitor: {}", request.getVisitorEmail());
    }

    /**
     * Send approval email to WALK-IN visitor (NO OTP - HTML formatted)
     */
    private void sendWalkInApprovalEmail(VisitorRequest request, Flat flat, User resident) {
        String subject = "✅ Walk-In Visitor Entry Approved";

        String htmlContent = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
                    .content { background-color: #f9f9f9; padding: 30px; border: 1px solid #ddd; }
                    .approved-box { background-color: #d4edda; border: 2px solid #4CAF50; padding: 20px; text-align: center; margin: 20px 0; border-radius: 5px; }
                    .details { background-color: #fff; padding: 15px; margin: 15px 0; border-left: 4px solid #4CAF50; }
                    .detail-row { padding: 5px 0; }
                    .label { font-weight: bold; color: #555; }
                    .note { background-color: #e7f3ff; border-left: 4px solid #2196F3; padding: 15px; margin: 15px 0; }
                    .footer { text-align: center; padding: 20px; color: #777; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>✅ Entry Approved</h1>
                    </div>
                    <div class="content">
                        <p>Dear <strong>%s</strong>,</p>
                        
                        <div class="approved-box">
                            <h2 style="margin: 0; color: #4CAF50;">🎉 Your Entry Has Been Approved!</h2>
                            <p style="margin: 10px 0 0 0; color: #666;">You may now proceed to enter the society premises.</p>
                        </div>
                        
                        <div class="details">
                            <h3 style="margin-top: 0; color: #4CAF50;">Visit Details</h3>
                            <div class="detail-row"><span class="label">🏠 Flat:</span> Block %s - %s</div>
                            <div class="detail-row"><span class="label">👤 Resident:</span> %s</div>
                            <div class="detail-row"><span class="label">📝 Purpose:</span> %s</div>
                            <div class="detail-row"><span class="label">⏰ Approved At:</span> %s</div>
                        </div>
                        
                        <div class="note">
                            <p style="margin: 0;"><strong>ℹ️ Note:</strong> As a walk-in visitor, you can proceed directly to the flat. No OTP is required for entry.</p>
                        </div>
                        
                        <p style="margin-top: 20px; text-align: center; color: #666;">
                            Please follow all society guidelines during your visit.
                        </p>
                    </div>
                    <div class="footer">
                        <p>This is an automated message from the Society Management System.</p>
                        <p>For any queries, please contact the society security desk.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                request.getVisitorName(),
                flat.getBlock(),
                flat.getNumber(),
                resident.getName(),
                request.getPurpose() != null ? request.getPurpose() : "Not specified",
                LocalDateTime.now().format(DATE_FORMATTER)
        );

        if (request.getVisitorEmail() != null && !request.getVisitorEmail().isEmpty()) {
            emailService.sendHtmlEmail(request.getVisitorEmail(), subject, htmlContent);
            log.info("Approval email sent to walk-in visitor: {}", request.getVisitorEmail());
        }
    }

    /**
     * Notify resident about pending walk-in visitor (HTML formatted)
     */
    private void notifyResidentAboutPendingVisitor(VisitorRequest request, Flat flat) {
        FlatMembersFilter filter = new FlatMembersFilter();
        filter.setFlat(flat.getId());
        List<FlatMember> residents = flatMembersService.getFlatMembers(filter);

        String subject = "🚨 Visitor Waiting at Gate – Approval Required";

        String htmlContent = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #FF9800; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
                    .content { background-color: #f9f9f9; padding: 30px; border: 1px solid #ddd; }
                    .alert-box { background-color: #fff3cd; border-left: 4px solid #FF9800; padding: 15px; margin: 15px 0; }
                    .visitor-details { background-color: #fff; padding: 15px; margin: 15px 0; border-left: 4px solid #FF9800; }
                    .detail-row { padding: 5px 0; }
                    .label { font-weight: bold; color: #555; }
                    .action-required { background-color: #f44336; color: white; padding: 15px; text-align: center; margin: 20px 0; border-radius: 5px; }
                    .footer { text-align: center; padding: 20px; color: #777; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🚨 Visitor Approval Required</h1>
                    </div>
                    <div class="content">
                        <p>Dear Resident,</p>
                        
                        <div class="alert-box">
                            <strong>⏰ Action Required:</strong> A walk-in visitor is waiting at the society gate and requires your approval to enter.
                        </div>
                        
                        <div class="visitor-details">
                            <h3 style="margin-top: 0; color: #FF9800;">Visitor Details</h3>
                            <div class="detail-row"><span class="label">👤 Name:</span> %s</div>
                            <div class="detail-row"><span class="label">📱 Phone:</span> %s</div>
                            <div class="detail-row"><span class="label">📝 Purpose:</span> %s</div>
                            <div class="detail-row"><span class="label">🏠 Flat:</span> Block %s - %s</div>
                            <div class="detail-row"><span class="label">⏰ Arrived At:</span> %s</div>
                        </div>
                        
                        <div class="action-required">
                            <h3 style="margin: 0 0 10px 0;">⚡ Immediate Action Required</h3>
                            <p style="margin: 0;">Please log in to the society management app to <strong>APPROVE</strong> or <strong>REJECT</strong> this visitor request.</p>
                        </div>
                        
                        <p style="margin-top: 20px; font-size: 14px; color: #666;">
                            <strong>Note:</strong> The visitor is waiting at the gate. Please review and respond promptly. Walk-in visitors don't require OTP - they can enter immediately upon your approval.
                        </p>
                    </div>
                    <div class="footer">
                        <p>This is an automated alert from the Society Security System.</p>
                        <p>Timestamp: %s</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                request.getVisitorName(),
                request.getVisitorPhone(),
                request.getPurpose() != null ? request.getPurpose() : "Not specified",
                flat.getBlock(),
                flat.getNumber(),
                LocalDateTime.now().format(DATE_FORMATTER),
                LocalDateTime.now().format(DATE_FORMATTER)
        );

        for (FlatMember resident : residents) {
            log.info("Notification sent to resident: {}", resident.getUser().getEmail());
            emailService.sendHtmlEmail(resident.getUser().getEmail(), subject, htmlContent);
        }

        log.info("Walk-in visitor notifications sent to residents of flat: {}", flat.getId());
    }
}