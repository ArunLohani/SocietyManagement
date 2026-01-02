package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.Visitor.GuestRequestDTO;
import com.project.societyManagement.dto.Visitor.VisitorResponseDTO;
import com.project.societyManagement.queryBuilder.visitorRequest.VisitorRequestFilter;
import com.project.societyManagement.service.VisitorRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/visitor-requests")
public class VisitorRequestController {

    private final VisitorRequestService visitorRequestService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VisitorResponseDTO>> getVisitorRequestById(@PathVariable Long id) {
        VisitorResponseDTO visitorResponseDTO = visitorRequestService.getVisitorRequestById(id);
        ApiResponse<VisitorResponseDTO> response = new ApiResponse<>(
                true,
                "Visitor request fetched successfully",
                visitorResponseDTO
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<VisitorResponseDTO>>> getVisitorRequestsList(
            @RequestBody VisitorRequestFilter filter) {

        List<VisitorResponseDTO> visitorsList = visitorRequestService.getAllVisitorRequests(filter);
        ApiResponse<List<VisitorResponseDTO>> response = new ApiResponse<>(
                true,
                "Visitor requests fetched successfully",
                visitorsList
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search-paginated")
    public ResponseEntity<ApiResponse<Page<VisitorResponseDTO>>> getVisitorRequestsPaginated(
            @RequestBody VisitorRequestFilter filter,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "6") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Page<VisitorResponseDTO> visitorsPage = visitorRequestService.getAllVisitorRequests(filter, pageable);
        ApiResponse<Page<VisitorResponseDTO>> response = new ApiResponse<>(
                true,
                "Visitor requests fetched successfully",
                visitorsPage
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<List<VisitorResponseDTO>>> getCurrentVisitors() {
        List<VisitorResponseDTO> currentVisitors = visitorRequestService.getCurrentVisitors();
        ApiResponse<List<VisitorResponseDTO>> response = new ApiResponse<>(
                true,
                "Current visitors fetched successfully",
                currentVisitors
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resident/create")
    public ResponseEntity<ApiResponse<VisitorResponseDTO>> createVisitorRequestByResident(
            @Valid @RequestBody GuestRequestDTO visitorRequestDTO) {

        VisitorResponseDTO response = visitorRequestService.createVisitorRequestByResident(visitorRequestDTO);
        ApiResponse<VisitorResponseDTO> apiResponse = new ApiResponse<>(
                true,
                "Visitor request created and approved successfully. OTP sent to visitor.",
                response
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VisitorResponseDTO>> updateVisitorRequest(
            @PathVariable Long id,
            @Valid @RequestBody GuestRequestDTO visitorRequestDTO) {

        VisitorResponseDTO response = visitorRequestService.updateVisitorRequest(id, visitorRequestDTO);
        ApiResponse<VisitorResponseDTO> apiResponse = new ApiResponse<>(
                true,
                "Visitor request updated successfully",
                response
        );
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<VisitorResponseDTO>> cancelVisitorRequest(@PathVariable Long id) {
        VisitorResponseDTO response = visitorRequestService.cancelVisitorRequest(id);
        ApiResponse<VisitorResponseDTO> apiResponse = new ApiResponse<>(
                true,
                "Visitor request cancelled successfully",
                response
        );
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<VisitorResponseDTO>> approveVisitorRequest(@PathVariable Long id) {
        VisitorResponseDTO response = visitorRequestService.approveVisitorRequest(id);
        ApiResponse<VisitorResponseDTO> apiResponse = new ApiResponse<>(
                true,
                "Visitor request approved successfully. OTP sent to visitor.",
                response
        );
        return ResponseEntity.ok(apiResponse);
    }


    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<VisitorResponseDTO>> rejectVisitorRequest(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody) {

        String reason = requestBody.getOrDefault("reason", "No reason provided");
        VisitorResponseDTO response = visitorRequestService.rejectVisitorRequest(id, reason);
        ApiResponse<VisitorResponseDTO> apiResponse = new ApiResponse<>(
                true,
                "Visitor request rejected successfully",
                response
        );
        return ResponseEntity.ok(apiResponse);
    }


    @PostMapping("/guard/walk-in")
    public ResponseEntity<ApiResponse<VisitorResponseDTO>> createWalkInVisitorRequest(
            @Valid @RequestBody GuestRequestDTO visitorRequestDTO) {

        VisitorResponseDTO response = visitorRequestService.createWalkInVisitorRequest(visitorRequestDTO);
        ApiResponse<VisitorResponseDTO> apiResponse = new ApiResponse<>(
                true,
                "Walk-in visitor request created. Awaiting resident approval.",
                response
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/{id}/verify-entry")
    public ResponseEntity<ApiResponse<VisitorResponseDTO>> verifyOtpAndMarkEntry(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody) {

        String otp = requestBody.get("otp");
        if (otp == null || otp.trim().isEmpty()) {
            ApiResponse<VisitorResponseDTO> apiResponse = new ApiResponse<>(
                    false,
                    "OTP is required",
                    null
            );
            return ResponseEntity.badRequest().body(apiResponse);
        }

        VisitorResponseDTO response = visitorRequestService.verifyOtpAndMarkEntry(id, otp);
        ApiResponse<VisitorResponseDTO> apiResponse = new ApiResponse<>(
                true,
                "Visitor entry verified successfully",
                response
        );
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{id}/mark-entry")
    public ResponseEntity<ApiResponse<VisitorResponseDTO>> markVisitorEntry(@PathVariable Long id) {
        VisitorResponseDTO response = visitorRequestService.markVisitorEntry(id);
        ApiResponse<VisitorResponseDTO> apiResponse = new ApiResponse<>(
                true,
                "Visitor Entry marked successfully",
                response
        );
        return ResponseEntity.ok(apiResponse);
    }


    @PostMapping("/{id}/mark-exit")
    public ResponseEntity<ApiResponse<VisitorResponseDTO>> markVisitorExit(@PathVariable Long id) {
        VisitorResponseDTO response = visitorRequestService.markVisitorExit(id);
        ApiResponse<VisitorResponseDTO> apiResponse = new ApiResponse<>(
                true,
                "Visitor exit marked successfully",
                response
        );
        return ResponseEntity.ok(apiResponse);
    }


    @PostMapping("/{id}/reject-entry")
    public ResponseEntity<ApiResponse<VisitorResponseDTO>> rejectVisitorEntry(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody) {

        String reason = requestBody.getOrDefault("reason", "Entry denied by security");
        VisitorResponseDTO response = visitorRequestService.rejectVisitorEntry(id, reason);
        ApiResponse<VisitorResponseDTO> apiResponse = new ApiResponse<>(
                true,
                "Visitor entry rejected successfully",
                response
        );
        return ResponseEntity.ok(apiResponse);
    }


    @PostMapping("/{id}/regenerate-otp")
    public ResponseEntity<ApiResponse<VisitorResponseDTO>> regenerateOtp(@PathVariable Long id) {
        VisitorResponseDTO response = visitorRequestService.regenerateOtp(id);
        ApiResponse<VisitorResponseDTO> apiResponse = new ApiResponse<>(
                true,
                "New OTP generated and sent to visitor successfully",
                response
        );
        return ResponseEntity.ok(apiResponse);
    }
}