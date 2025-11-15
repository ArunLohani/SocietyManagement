package com.project.societyManagement.service;

import com.project.societyManagement.dto.Notice.NoticeCreationRequest;
import com.project.societyManagement.entity.Notice;
import com.project.societyManagement.queryBuilder.notice.NoticeFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeService {
    public Notice createNotice(NoticeCreationRequest noticeRequest);
    public Notice getNoticeById(Long noticeId);
    public Notice updateNotice(Long noticeId , NoticeCreationRequest noticeCreationRequest);
    public Notice togglePublicStatus(Long noticeId);
    public Notice toggleExpiryStatus(Long noticeId);
    public Page<Notice> getNoticesForTenant(Long tenantId , Pageable pageable);
    public Page<Notice> searchNotices(NoticeFilter noticeFilter, Pageable pageable);
}
