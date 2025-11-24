package com.project.societyManagement.queryBuilder.parkingSlot;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.entity.ParkingSlot;
import com.project.societyManagement.entity.Role;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.Vehicle;
import com.project.societyManagement.entity.types.ParkingSlotStatus;
import com.project.societyManagement.queryBuilder.core.AbstractFilterableQueryBuilder;
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
public class ParkingSlotQueryBuilder extends AbstractFilterableQueryBuilder<ParkingSlot, ParkingSlotFilter> {


    ParkingSlotQueryBuilder(EntityManager entityManager , CriteriaBuilderFactory cbf ){
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
    protected Class<ParkingSlot> getEntityClass() {
        return ParkingSlot.class;
    }

    @Override
    protected String getEntityAlias() {
        return "ps";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<ParkingSlot> cb){
        Set<String> roles = getLoggedInUserRole();
        if (roles.contains("ADMIN")){
            cb.where("ps.tenant.id").eq(TenantContextHolder.getCurrentTenant());
            return ;
        }
        cb.where("ps.user.id").eq(getCurrentUser().getId());
    }

    @Override
    public void applyFilters(CriteriaBuilder<ParkingSlot> cb,ParkingSlotFilter filter){
        if(filter.getId()!=null) cb.where("ps.id").eq(filter.getId());
        if(filter.getArea() != null) cb.where("ps.area").eq(filter.getArea());
        if(filter.getSlotNumber() != null) cb.where("ps.slotNumber").eq(filter.getSlotNumber());
        if (filter.getUser()!=null) cb.where("ps.user.id").eq(filter.getUser());
        if (filter.getStatus()!=null) cb.where("ps.status").eq(ParkingSlotStatus.valueOf(filter.getStatus()));
        if (filter.getTenant()!=null) cb.where("ps.tenant.id").eq(filter.getTenant());
    }

}
