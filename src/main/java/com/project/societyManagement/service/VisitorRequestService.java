package com.project.societyManagement.service;

import com.project.societyManagement.dto.Visitor.GuestRequestDTO;
import com.project.societyManagement.dto.Visitor.VisitorResponseDTO;
import com.project.societyManagement.entity.VisitorRequest;
import com.project.societyManagement.queryBuilder.visitorRequest.VisitorRequestFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface VisitorRequestService {
    public VisitorRequest findVisitorRequestById(Long id);
    public Page<VisitorResponseDTO> getAllVisitorRequests(VisitorRequestFilter filter, Pageable pageable);
    public List<VisitorResponseDTO> getAllVisitorRequests(VisitorRequestFilter filter) ;
    public VisitorResponseDTO getVisitorRequestById(Long id);
    public VisitorResponseDTO createVisitorRequestByResident(GuestRequestDTO visitorRequest) ;
    public VisitorResponseDTO updateVisitorRequest(Long id, GuestRequestDTO visitorRequestDTO);
    public VisitorResponseDTO cancelVisitorRequest(Long id);
    public VisitorResponseDTO createWalkInVisitorRequest(GuestRequestDTO visitorRequest);
    public VisitorResponseDTO verifyOtpAndMarkEntry(Long id, String otp);
    public VisitorResponseDTO markVisitorExit(Long id);
    public VisitorResponseDTO rejectVisitorEntry(Long id, String reason);
    public VisitorResponseDTO approveVisitorRequest(Long id);
    public VisitorResponseDTO rejectVisitorRequest(Long id, String reason) ;
    public VisitorResponseDTO regenerateOtp(Long id);
    public List<VisitorResponseDTO> getCurrentVisitors();
    public VisitorResponseDTO markVisitorEntry(Long id);
}
