package com.project.societyManagement.queryBuilder.event;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.entity.*;
import com.project.societyManagement.queryBuilder.core.AbstractFilterableQueryBuilder;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EventQueryBuilder extends AbstractFilterableQueryBuilder<Event, EventFilter> {

    EventQueryBuilder(EntityManager entityManager , CriteriaBuilderFactory cbf){
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
    protected Class<Event> getEntityClass() {
        return Event.class;
    }

    @Override
    protected String getEntityAlias() {
        return "e";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<Event> cb){

        Set<String> roles = getLoggedInUserRole();
        if(roles.contains("SUPER_ADMIN")){
            return;
        }
            cb.where("e.tenant.id").eq(TenantContextHolder.getCurrentTenant());




    }

    @Override
    public void applyFilters(CriteriaBuilder<Event> cb,EventFilter filter){
        if(filter.getId()!=null) cb.where("e.id").eq(filter.getId());
        if(filter.getName() != null) cb.where("e.name").eq(filter.getName());
        if(filter.getDescription() != null) cb.where("e.description").eq(filter.getDescription());
        if(filter.getStatus() != null) cb.where("e.status").eq(filter.getStatus());
        if(filter.getLocation() != null) cb.where("e.location").eq(filter.getLocation());
        if (filter.getOrganizedBy()!=null)cb.where("e.organizedBy.id").eq(filter.getOrganizedBy());
        if (filter.getStartDateTime()!=null) cb.where("e.startDateTime").ge(filter.getStartDateTime());
        if (filter.getEndDateTime()!=null)cb.where("e.endStartTime").ge(filter.getEndDateTime());
        if (filter.getTenantId()!=null) cb.where("e.tenant.id").eq(filter.getTenantId());
    }
}
