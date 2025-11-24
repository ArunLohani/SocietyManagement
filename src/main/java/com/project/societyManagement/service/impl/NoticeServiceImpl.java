package com.project.societyManagement.service.impl;

import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.dto.Notice.NoticeCreationRequest;
import com.project.societyManagement.entity.Notice;
import com.project.societyManagement.entity.Tenant;
import com.project.societyManagement.queryBuilder.notice.NoticeFilter;
import com.project.societyManagement.queryBuilder.notice.NoticeQueryBuilder;
import com.project.societyManagement.repository.NoticeRepository;
import com.project.societyManagement.service.NoticeService;
import com.project.societyManagement.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeQueryBuilder noticeQueryBuilder;
    private final ModelMapper modelMapper;
    private final TenantService tenantService;

    public Notice createNotice(NoticeCreationRequest noticeRequest){
        Notice notice = modelMapper.map(noticeRequest,Notice.class);
        Tenant tenant = tenantService.findTenantById(TenantContextHolder.getCurrentTenant());
        notice.setTenant(tenant);
        return noticeRepository.save(notice);
    }

    public Notice getNoticeById(Long noticeId) {
        NoticeFilter noticeFilter = new NoticeFilter();
        noticeFilter.setId(noticeId);
        Notice notice = noticeQueryBuilder.findById(noticeFilter);
        return notice;
    }

    public Notice updateNotice(Long noticeId , NoticeCreationRequest noticeCreationRequest){
        Notice notice = getNoticeById(noticeId);
        notice = Notice.builder()
                .title(noticeCreationRequest.getTitle())
                .message(noticeCreationRequest.getMessage())
                .isExpired(noticeCreationRequest.getIsExpired())
                .isPublic(noticeCreationRequest.getIsPublic())
                .category(noticeCreationRequest.getCategory())
                .build();
        return noticeRepository.save(notice);
    }

    public Notice deleteNotice(Long noticeId){
        Notice notice = getNoticeById(noticeId);
        notice.setActive(false);
        return noticeRepository.save(notice);
    }
    public Notice togglePublicStatus(Long noticeId){
        Notice notice = getNoticeById(noticeId);
        notice.setIsPublic(!notice.getIsPublic());
        return noticeRepository.save(notice);
    }

    public Notice toggleExpiryStatus(Long noticeId){
        Notice notice = getNoticeById(noticeId);
        notice.setIsExpired(!notice.getIsExpired());
        return noticeRepository.save(notice);
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
