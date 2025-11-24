package com.project.societyManagement.service.impl;

import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.dto.Complaints.ComplaintIssuingRequest;
import com.project.societyManagement.entity.Complaints;
import com.project.societyManagement.entity.Tenant;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.ComplaintStatus;
import com.project.societyManagement.entity.types.Priority;
import com.project.societyManagement.queryBuilder.complaints.ComplaintsFilter;
import com.project.societyManagement.queryBuilder.complaints.ComplaintsQueryBuilder;
import com.project.societyManagement.repository.ComplaintsRepo;
import com.project.societyManagement.service.ComplaintsService;
import com.project.societyManagement.service.TenantService;
import com.project.societyManagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ComplaintsServiceImpl implements ComplaintsService {

    private final ComplaintsQueryBuilder complaintsQueryBuilder;
    private final ComplaintsRepo complaintsRepo;
    private final ModelMapper modelMapper;
    private final TenantService tenantService;
    private final UserService userService;

    public Complaints getComplaintById(Long complaintId){
        ComplaintsFilter filter = new ComplaintsFilter();
        filter.setId(complaintId);
        Complaints complaints = complaintsQueryBuilder.findById(filter);
        return complaints;
    }

    public Complaints issueComplaint(ComplaintIssuingRequest complaintRequest){
        Complaints complaint = modelMapper.map(complaintRequest,Complaints.class);
        Tenant tenant = tenantService.findTenantById(TenantContextHolder.getCurrentTenant());
        User user = userService.findUserById(complaintRequest.getRaisedByUser());
        complaint.setTenant(tenant);
        complaint.setRaisedByUser(user);
        complaint.setPriority(Priority.valueOf(complaintRequest.getPriority()));
        complaint = complaintsRepo.save(complaint);
        return complaint;
    }

    public Complaints updateComplaint(Long complaintId,ComplaintIssuingRequest complaintRequest){
            Complaints complaints = getComplaintById(complaintId);
            complaints.setTitle(complaintRequest.getTitle());
            complaints.setDescription(complaintRequest.getDescription());
            complaints.setCategory(complaintRequest.getCategory());
            complaints.setPriority(Priority.valueOf(complaintRequest.getPriority()));
            return complaintsRepo.save(complaints);
    }

    public Complaints deleteComplaint(Long complaintId){
        Complaints complaints = getComplaintById(complaintId);
        complaints.setIsActive(false);
        return complaintsRepo.save(complaints);
    }

    public Complaints assignComplaint(Long complaintId, Long userId) {
        Complaints complaints = getComplaintById(complaintId);
        User user = userService.findUserById(userId);
        complaints.setAssignedToUser(user);
        return complaintsRepo.save(complaints);
    }

    public Complaints changeComplaintStatus(Long complaintId , String status){
        Complaints complaints = getComplaintById(complaintId);
        complaints.setStatus(ComplaintStatus.valueOf(status));
        return complaintsRepo.save(complaints);
    }

    public Page<Complaints> listComplaintsByUser(Long userId , Pageable pageable){
        ComplaintsFilter complaintsFilter = new ComplaintsFilter();
        complaintsFilter.setRaisedByUser(userId);
        Page<Complaints> complaints = complaintsQueryBuilder.searchPaginated(complaintsFilter,pageable);
        return complaints;
    }


    public Page<Complaints> listComplaintsAssignedToUser(Long userId , Pageable pageable){
        ComplaintsFilter complaintsFilter = new ComplaintsFilter();
        complaintsFilter.setAssignedToUser(userId);
        Page<Complaints> complaints = complaintsQueryBuilder.searchPaginated(complaintsFilter,pageable);
        return complaints;
    }

    public Page<Complaints> searchComplaints(ComplaintsFilter filter , Pageable pageable){
        Page<Complaints> complaints = complaintsQueryBuilder.searchPaginated(filter,pageable);
        return complaints;
    }

    public Complaints addResolutionNotes(Long complaintId , String note){
        Complaints complaints = getComplaintById(complaintId);
        complaints.setResolutionNotes(note);
        return complaintsRepo.save(complaints);
    }

}
