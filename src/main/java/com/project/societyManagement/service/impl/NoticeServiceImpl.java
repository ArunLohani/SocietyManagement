package com.project.societyManagement.service.impl;

import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.dto.Notice.NoticeCreationRequest;
import com.project.societyManagement.entity.Notice;
import com.project.societyManagement.entity.Tenant;
import com.project.societyManagement.queryBuilder.notice.NoticeFilter;
import com.project.societyManagement.queryBuilder.notice.NoticeQueryBuilder;
import com.project.societyManagement.repository.NoticeRepo;
import com.project.societyManagement.service.NoticeService;
import com.project.societyManagement.service.NotificationService;
import com.project.societyManagement.service.TenantService;
import com.project.societyManagement.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepo noticeRepo;
    private final NoticeQueryBuilder noticeQueryBuilder;
    private final ModelMapper modelMapper;
    private final TenantService tenantService;
    private final NotificationService notificationService;
    private final ValidationUtil validationUtil;

    public Notice createNotice(NoticeCreationRequest noticeRequest) {
        validationUtil.validate(noticeRequest);
        Notice notice = modelMapper.map(noticeRequest, Notice.class);
        Tenant tenant = tenantService.findTenantById(TenantContextHolder.getCurrentTenant());
        notice.setTenant(tenant);
        notice = noticeRepo.save(notice);
        notificationService.notifySociety(
                notice.getTenant().getId(),
                "New Notice: " + notice.getTitle(),
                "A new notice has been published: " + notice.getTitle(), "/notices/" + notice.getId()
        );
        return notice;
    }

    public Notice getNoticeById(Long noticeId) {
        NoticeFilter noticeFilter = new NoticeFilter();
        noticeFilter.setId(noticeId);
        Notice notice = noticeQueryBuilder.findById(noticeFilter);
        return notice;
    }

    public Notice updateNotice(Long noticeId , NoticeCreationRequest noticeCreationRequest){
        validationUtil.validate(noticeCreationRequest);
        Notice notice = getNoticeById(noticeId);
        notice = Notice.builder()
                .title(noticeCreationRequest.getTitle())
                .message(noticeCreationRequest.getMessage())
                .isExpired(noticeCreationRequest.getIsExpired())
                .isPublic(noticeCreationRequest.getIsPublic())
                .category(noticeCreationRequest.getCategory())
                .build();
        return noticeRepo.save(notice);
    }

    public Notice deleteNotice(Long noticeId){
        Notice notice = getNoticeById(noticeId);
        notice.setIsActive(false);
        return noticeRepo.save(notice);
    }
    public Notice togglePublicStatus(Long noticeId){
        Notice notice = getNoticeById(noticeId);
        notice.setIsPublic(!notice.getIsPublic());
        return noticeRepo.save(notice);
    }

    public Notice toggleExpiryStatus(Long noticeId){
        Notice notice = getNoticeById(noticeId);
        notice.setIsExpired(!notice.getIsExpired());
        return noticeRepo.save(notice);
    }

    public Page<Notice> getNoticesForTenant(Long tenantId , Pageable pageable){
        NoticeFilter noticeFilter = new NoticeFilter();
        noticeFilter.setTenantId(tenantId);
        Page<Notice> notices = noticeQueryBuilder.searchPaginated(noticeFilter,pageable);
        return notices;
    }

    public Page<Notice> searchNotices(NoticeFilter noticeFilter, Pageable pageable){
        Page<Notice> notices = noticeQueryBuilder.searchPaginated(noticeFilter,pageable);
        return notices;
    }

}
