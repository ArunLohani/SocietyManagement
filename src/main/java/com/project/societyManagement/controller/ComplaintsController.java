package com.project.societyManagement.controller;

import com.project.societyManagement.annotations.RequiresPermission;
import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.Complaints.ComplaintIssuingRequest;
import com.project.societyManagement.entity.Complaints;
import com.project.societyManagement.queryBuilder.complaints.ComplaintsFilter;
import com.project.societyManagement.service.ComplaintsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class ComplaintsController {

    private final ComplaintsService complaintsService;

    @RequiresPermission(api = "CREATE_COMPLAINTS")
    @PostMapping
    public ResponseEntity<ApiResponse<Complaints>> issueComplaint(
            @RequestBody ComplaintIssuingRequest complaintRequest) {

        Complaints complaint = complaintsService.issueComplaint(complaintRequest);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "Complaint issued successfully", complaint),
                HttpStatus.CREATED
        );
    }

    @RequiresPermission(api = "EDIT_COMPLAINTS")
    @PutMapping("/{complaintId}")
    public ResponseEntity<ApiResponse<Complaints>> updateComplaint(@PathVariable Long complaintId,
            @RequestBody ComplaintIssuingRequest complaintRequest) {

        Complaints complaint = complaintsService.updateComplaint(complaintId,complaintRequest);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "Complaint updated successfully", complaint),
                HttpStatus.CREATED
        );
    }

    @RequiresPermission(api = "CREATE_COMPLAINTS")
    @DeleteMapping("/{complaintId}")
    public ResponseEntity<ApiResponse<Complaints>> deleteComplaint(@PathVariable Long complaintId) {

        Complaints complaint = complaintsService.deleteComplaint(complaintId);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "Complaint deleted successfully", complaint),
                HttpStatus.CREATED
        );
    }

    @RequiresPermission(api = "SEARCH_COMPLAINTS")
    @GetMapping("/{complaintId}")
    public ResponseEntity<ApiResponse<Complaints>> getComplaintById(@PathVariable Long complaintId) {
        Complaints complaint = complaintsService.getComplaintById(complaintId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Complaint fetched successfully", complaint)
        );
    }

    @RequiresPermission(api = "EDIT_COMPLAINTS")
    @PutMapping("/{complaintId}/assign/{userId}")
    public ResponseEntity<ApiResponse<Complaints>> assignComplaint(
            @PathVariable Long complaintId,
            @PathVariable Long userId) {

        Complaints updated = complaintsService.assignComplaint(complaintId, userId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Complaint assigned successfully", updated)
        );
    }

    @RequiresPermission(api = "EDIT_COMPLAINTS")
    @PatchMapping("/{complaintId}/status")
    public ResponseEntity<ApiResponse<Complaints>> changeComplaintStatus(
            @PathVariable Long complaintId,
            @RequestParam String status) {

        Complaints updated = complaintsService.changeComplaintStatus(complaintId, status);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Complaint status updated", updated)
        );
    }

    @RequiresPermission(api = "SEARCH_COMPLAINTS")
    @GetMapping("/raised-by/{userId}")
    public ResponseEntity<ApiResponse<Page<Complaints>>> listComplaintsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Complaints> complaints = complaintsService.listComplaintsByUser(userId, pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Complaints raised by user fetched successfully", complaints)
        );
    }

    @RequiresPermission(api = "SEARCH_COMPLAINTS")
    @GetMapping("/assigned-to/{userId}")
    public ResponseEntity<ApiResponse<Page<Complaints>>> listComplaintsAssignedToUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Complaints> complaints = complaintsService.listComplaintsAssignedToUser(userId, pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Complaints assigned to user fetched successfully", complaints)
        );
    }

    @RequiresPermission(api = "SEARCH_COMPLAINTS")
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<Complaints>>> searchComplaints(
            @RequestBody ComplaintsFilter filter,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Complaints> complaints = complaintsService.searchComplaints(filter, pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Complaints fetched successfully", complaints)
        );
    }

    @RequiresPermission(api = "EDIT_COMPLAINTS")
    @PatchMapping("/{complaintId}/resolution")
    public ResponseEntity<ApiResponse<Complaints>> addResolutionNotes(
            @PathVariable Long complaintId,
            @RequestBody String note) {

        Complaints updated = complaintsService.addResolutionNotes(complaintId, note);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Resolution notes added successfully", updated)
        );
    }
}
