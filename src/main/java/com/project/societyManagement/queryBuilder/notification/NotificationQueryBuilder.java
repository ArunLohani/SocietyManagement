package com.project.societyManagement.queryBuilder.notification;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.entity.*;
import com.project.societyManagement.entity.types.NotificationType;
import com.project.societyManagement.queryBuilder.core.AbstractFilterableQueryBuilder;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class NotificationQueryBuilder extends AbstractFilterableQueryBuilder<Notification, NotificationFilter> {

    NotificationQueryBuilder(EntityManager entityManager , CriteriaBuilderFactory cbf){
        super(entityManager,cbf);
    }

    private User getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth==null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken){
            return null;
        }
        return (User) auth.getPrincipal();
    }

    public Set<String> getLoggedInUserRole(){
        User user = getCurrentUser();
        if(user == null){
            return Collections.emptySet();
        }
        return user.getRoles().stream().map(Role::getRole).collect(Collectors.toSet());
    }

    public Long getLoggedInUserId(){
        User user = getCurrentUser();
        return user.getId();
    }

    public Long getLoggedInUserTenantId(){
        User user = getCurrentUser();
        log.info( "Tenant Id : {}", user.getTenant().getId().toString());
        return user.getTenant().getId();
    }

    @Override
    protected Class<Notification> getEntityClass() {
        return Notification.class;
    }

    @Override
    protected String getEntityAlias() {
        return "n";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<Notification> cb){

    }

    @Override
    public void applyFilters(CriteriaBuilder<Notification> cb,NotificationFilter filter){
        if(filter.getId()!=null) cb.where("n.id").eq(filter.getId());
        if(filter.getTitle() != null) cb.where("n.title").eq(filter.getTitle());
        if(filter.getMessage() != null) cb.where("n.message").eq(filter.getMessage());
        if(filter.getIsActive() != null) cb.where("n.isActive").eq(filter.getIsActive());
        if(filter.getType() != null) cb.where("n.type").eq(NotificationType.valueOf(filter.getType()));
        if (filter.getUrl()!=null)cb.where("n.url").eq(filter.getUrl());
        if (filter.getUserId()!=null) cb.where("n.userId").eq(filter.getUserId());
        if (filter.getSocietyId()!=null) cb.where("n.societyId").eq(filter.getSocietyId());
        if (filter.getRead() != null) cb.where("n.read").eq(filter.getRead());
        if (filter.getCreatedAt() != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneDayAgo = now.minusDays(1);
            cb.where("n.createdAt").ge(oneDayAgo);
            cb.where("n.createdAt").le(now);

        }

        if (filter.getSortFilter() != null){
            applySorting(cb,filter.getSortFilter().getProperty(),filter.getSortFilter().getAsc());
        }


    }
}
