package com.project.societyManagement.service.impl;

import com.project.societyManagement.annotations.Auditing;
import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.dto.Notice.NoticeCreationRequest;
import com.project.societyManagement.entity.Notice;
import com.project.societyManagement.entity.Tenant;
import com.project.societyManagement.kafka.dto.NoticeCreatedEvent;
import com.project.societyManagement.kafka.producer.NoticeEventProducer;
import com.project.societyManagement.queryBuilder.notice.NoticeFilter;
import com.project.societyManagement.queryBuilder.notice.NoticeQueryBuilder;
import com.project.societyManagement.repository.NoticeRepo;
import com.project.societyManagement.service.NoticeService;
import com.project.societyManagement.service.NotificationService;
import com.project.societyManagement.service.TenantService;
import com.project.societyManagement.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepo noticeRepo;
    private final NoticeQueryBuilder noticeQueryBuilder;
    private final ModelMapper modelMapper;
    private final TenantService tenantService;
    private final NotificationService notificationService;
    private final ValidationUtil validationUtil;
    private final NoticeEventProducer noticeEventProducer;

    @Auditing(entity = "Notices",action = "CREATE")
    public Notice createNotice(NoticeCreationRequest noticeRequest) {
        log.info("{}",noticeRequest);
        validationUtil.validate(noticeRequest);
        Notice notice = modelMapper.map(noticeRequest, Notice.class);
        Tenant tenant = tenantService.findTenantById(TenantContextHolder.getCurrentTenant());
        notice.setTenant(tenant);
        notice = noticeRepo.save(notice);
       noticeEventProducer.publishNoticeCreated(new NoticeCreatedEvent(notice.getId(),  notice.getTitle(),notice.getTenant().getId()));
        return notice;
    }

    @Auditing(entity = "Notices",action = "READ")
    public Notice getNoticeById(Long noticeId) {
        NoticeFilter noticeFilter = new NoticeFilter();
        noticeFilter.setId(noticeId);
        Notice notice = noticeQueryBuilder.findById(noticeFilter);
        return notice;
    }

    @Auditing(entity = "Notices",action = "EDIT")
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

    @Auditing(entity = "Notices",action = "DELETE")
    public Notice deleteNotice(Long noticeId){
        Notice notice = getNoticeById(noticeId);
        notice.setIsActive(false);
        return noticeRepo.save(notice);
    }

    @Auditing(entity = "Notices",action = "EDIT")
    public Notice togglePublicStatus(Long noticeId){
        Notice notice = getNoticeById(noticeId);
        notice.setIsPublic(!notice.getIsPublic());
        return noticeRepo.save(notice);
    }

    @Auditing(entity = "Notices",action = "EDIT")
    public Notice toggleExpiryStatus(Long noticeId){
        Notice notice = getNoticeById(noticeId);
        notice.setIsExpired(!notice.getIsExpired());
        return noticeRepo.save(notice);
    }
    @Auditing(entity = "Notices",action = "READ")
    public Page<Notice> getNoticesForTenant(Long tenantId , Pageable pageable){
        NoticeFilter noticeFilter = new NoticeFilter();
        noticeFilter.setTenantId(tenantId);
        Page<Notice> notices = noticeQueryBuilder.searchPaginated(noticeFilter,pageable);
        return notices;
    }

    @Auditing(entity = "Notices",action = "READ")
    public Page<Notice> searchNotices(NoticeFilter noticeFilter, Pageable pageable){
        Page<Notice> notices = noticeQueryBuilder.searchPaginated(noticeFilter,pageable);
        return notices;
    }

}
