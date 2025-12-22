package com.project.societyManagement.queryBuilder.vehicle;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.entity.FlatMember;
import com.project.societyManagement.entity.Role;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.Vehicle;
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
public class VehicleQueryBuilder extends AbstractFilterableQueryBuilder<Vehicle, VehicleFilter> {


    VehicleQueryBuilder(EntityManager entityManager , CriteriaBuilderFactory cbf ){
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
    protected Class<Vehicle> getEntityClass() {
        return Vehicle.class;
    }

    @Override
    protected String getEntityAlias() {
        return "v";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<Vehicle> cb){
        Set<String> roles = getLoggedInUserRole();
        if (roles.contains("ADMIN")){
            cb.where("v.owningFlat.tenant.id").eq(TenantContextHolder.getCurrentTenant());
            return ;
        }
        cb.whereExists()
                .from(FlatMember.class, "fm")
                .where("fm.flat.id").eqExpression("v.owningFlat.id")
                .end();

    }

    @Override
    public void applyFilters(CriteriaBuilder<Vehicle> cb,VehicleFilter filter) {
        if (filter.getId() != null) cb.where("v.id").eq(filter.getId());
        if (filter.getRegistrationNumber() != null) cb.where("v.registrationNumber").eq(filter.getRegistrationNumber());
        if (filter.getBrand() != null) cb.where("v.brand").eq(filter.getBrand());

        if (filter.getModel() != null) cb.where("v.model").eq(filter.getModel());
        if (filter.getVehicleType() != null) cb.where("v.vehicleType").eq(filter.getVehicleType());
        if (filter.getOwner() != null) cb.where("v.owningFlat.id").eq(filter.getOwner());
        if (filter.getIsActive() != null) cb.where("v.isActive").eq(filter.getIsActive());
        if (filter.getUser()!=null)
            cb.whereExists()
                    .from(FlatMember.class, "fm")
                    .where("fm.flat.id").eqExpression("v.owningFlat.id")
                    .where("fm.user.id").eq(filter.getUser())
                    .where("fm.isActive").eq(true)
                    .end();
    }
}
