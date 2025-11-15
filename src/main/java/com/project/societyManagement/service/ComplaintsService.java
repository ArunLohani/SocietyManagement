package com.project.societyManagement.service;

import com.project.societyManagement.dto.Complaints.ComplaintIssuingRequest;
import com.project.societyManagement.entity.Complaints;
import com.project.societyManagement.queryBuilder.compaints.ComplaintsFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ComplaintsService {

    public Complaints getComplaintById(Long complaintId);
    public Complaints issueComplaint(ComplaintIssuingRequest complaintRequest);
    public Complaints assignComplaint(Long complaintId, Long userId);
    public Complaints changeComplaintStatus(Long complaintId , String status);
    public Page<Complaints> listComplaintsByUser(Long userId , Pageable pageable);
    public Page<Complaints> listComplaintsAssignedToUser(Long userId , Pageable pageable);
    public Page<Complaints> searchComplaints(ComplaintsFilter filter , Pageable pageable);
    public Complaints addResolutionNotes(Long complaintId , String note);
}
