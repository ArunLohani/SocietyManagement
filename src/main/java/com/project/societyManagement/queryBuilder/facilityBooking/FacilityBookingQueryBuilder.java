package com.project.societyManagement.queryBuilder.facilityBooking;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.entity.*;
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

@Component
@Slf4j
public class FacilityBookingQueryBuilder extends AbstractFilterableQueryBuilder<FacilityBooking, FacilityBookingFilter> {

    FacilityBookingQueryBuilder(EntityManager entityManager, CriteriaBuilderFactory cbf) {
        super(entityManager, cbf);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return (User) auth.getPrincipal();
    }

    public Set<String> getLoggedInUserRole() {
        User user = getCurrentUser();
        if (user == null) {
            return Collections.emptySet();
        }
        return user.getRoles().stream().map(Role::getRole).collect(Collectors.toSet());
    }

    public Long getLoggedInUserId() {
        User user = getCurrentUser();
        return user.getId();
    }

    public Long getLoggedInUserTenantId() {
        User user = getCurrentUser();
        log.info("Tenant Id : {}", user.getTenant().getId().toString());
        return user.getTenant().getId();
    }

    @Override
    protected Class<FacilityBooking> getEntityClass() {
        return FacilityBooking.class;
    }

    @Override
    protected String getEntityAlias() {
        return "fb";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<FacilityBooking> cb) {
        Set<String> roles = getLoggedInUserRole();
        if (roles.contains("SUPER_ADMIN")) {
            return;
        }

        if (roles.contains("ADMIN")){
            cb.where("fb.user.tenant.id").eq(TenantContextHolder.getCurrentTenant());
        }

        cb.where("fb.user.id").eq(getCurrentUser().getId());
    }

    @Override
    public void applyFilters(CriteriaBuilder<FacilityBooking> cb, FacilityBookingFilter filter) {
        if (filter.getId() != null) cb.where("fb.id").eq(filter.getId());
        if (filter.getFacility() != null) cb.where("fb.facility.id").eq(filter.getFacility());
        if (filter.getUser() != null) cb.where("fb.user.id").eq(filter.getUser());
        if (filter.getIsActive()!=null) cb.where("fb.isActive").eq(filter.getIsActive());

    }
}