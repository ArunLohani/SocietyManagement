package com.project.societyManagement.controller;

import com.project.societyManagement.annotations.RequiresPermission;
import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.Notice.NoticeCreationRequest;
import com.project.societyManagement.entity.Notice;
import com.project.societyManagement.queryBuilder.notice.NoticeFilter;
import com.project.societyManagement.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @RequiresPermission(api = "CREATE_NOTICES")
    @PostMapping
    public ResponseEntity<ApiResponse<Notice>> createNotice(
            @RequestBody NoticeCreationRequest request
    ) {
        Notice notice = noticeService.createNotice(request);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "Notice created successfully", notice),
                HttpStatus.CREATED
        );
    }

    @RequiresPermission(api = "SEARCH_NOTICES")
    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<Notice>> getNoticeById(
            @PathVariable Long noticeId
    ) {
        Notice notice = noticeService.getNoticeById(noticeId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notice fetched successfully", notice)
        );
    }

    @RequiresPermission(api = "EDIT_NOTICES")
    @PutMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<Notice>> updateNotice(
            @PathVariable Long noticeId,
            @RequestBody NoticeCreationRequest request
    ) {
        Notice notice = noticeService.updateNotice(noticeId, request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notice updated successfully", notice)
        );
    }

    @RequiresPermission(api = "CREATE_NOTICES")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<Notice>> deleteNotice(
            @PathVariable Long noticeId
    ) {
        Notice notice = noticeService.deleteNotice(noticeId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notice delete successfully", notice)
        );
    }

    @RequiresPermission(api = "EDIT_NOTICES")
    @PatchMapping("/{noticeId}/toggle-public")
    public ResponseEntity<ApiResponse<Notice>> togglePublic(
            @PathVariable Long noticeId
    ) {
        Notice notice = noticeService.togglePublicStatus(noticeId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Public status toggled", notice)
        );
    }

    @RequiresPermission(api = "EDIT_NOTICES")
    @PatchMapping("/{noticeId}/toggle-expired")
    public ResponseEntity<ApiResponse<Notice>> toggleExpired(
            @PathVariable Long noticeId
    ) {
        Notice notice = noticeService.toggleExpiryStatus(noticeId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Expired status toggled", notice)
        );
    }

    @RequiresPermission(api = "SEARCH_NOTICES")
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<Page<Notice>>> getNoticesForTenant(
            @PathVariable Long tenantId,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Notice> notices = noticeService.getNoticesForTenant(tenantId, pageable);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notices fetched successfully", notices)
        );
    }

    @RequiresPermission(api = "SEARCH_NOTICES")
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<Notice>>> searchNotices(
            @RequestBody NoticeFilter filter,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Notice> notices = noticeService.searchNotices(filter, pageable);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notices fetched successfully", notices)
        );
    }
}
