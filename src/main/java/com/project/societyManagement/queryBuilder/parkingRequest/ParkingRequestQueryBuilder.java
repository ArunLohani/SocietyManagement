package com.project.societyManagement.queryBuilder.parkingRequest;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.entity.*;
import com.project.societyManagement.entity.types.ParkingRequestStatus;
import com.project.societyManagement.entity.types.ParkingSlotStatus;
import com.project.societyManagement.queryBuilder.core.AbstractFilterableQueryBuilder;
import com.project.societyManagement.queryBuilder.parkingSlot.ParkingSlotFilter;
import com.project.societyManagement.queryBuilder.user.UserFilter;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j
@Component
public class ParkingRequestQueryBuilder extends AbstractFilterableQueryBuilder<ParkingRequest, ParkingRequestFilter> {


    ParkingRequestQueryBuilder(EntityManager entityManager , CriteriaBuilderFactory cbf ){
        super(entityManager,cbf);
    }

    private User getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth==null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken){
            return null;
        }
        Object principal = auth.getPrincipal();
        //Check if principal is actually User entity
        if(principal instanceof User){
            return (User) principal;
        }
        //During Oauth2 login, principal might be of Oauth2User
        return null;
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
    protected Class<ParkingRequest> getEntityClass() {
        return ParkingRequest.class;
    }

    @Override
    protected String getEntityAlias() {
        return "pr";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<ParkingRequest> cb){
        Set<String> roles = getLoggedInUserRole();
        if (roles.contains("ADMIN")){
            cb.where("pr.user.tenant.id").eq(TenantContextHolder.getCurrentTenant());
            return ;
        }
        cb.where("pr.user.id").eq(getCurrentUser().getId());
    }

    @Override
    public void applyFilters(CriteriaBuilder<ParkingRequest> cb,ParkingRequestFilter filter){
        if(filter.getId()!=null) cb.where("pr.id").eq(filter.getId());
        if(filter.getUser() != null) cb.where("pr.user.id").eq(filter.getUser());

        if (filter.getRequestedSlot()!=null) cb.where("pr.requestedSlot.id").eq(filter.getRequestedSlot());
        if (filter.getStatus()!=null) cb.where("pr.status").eq(ParkingRequestStatus.valueOf(filter.getStatus()));
        if (filter.getAdminComment()!=null) cb.where("pr.adminComment").eq(filter.getAdminComment());
    }

}
