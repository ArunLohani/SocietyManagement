package com.project.societyManagement.queryBuilder.facility;

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
public class FacilityQueryBuilder extends AbstractFilterableQueryBuilder<Facility, FacilityFilter> {

    FacilityQueryBuilder(EntityManager entityManager, CriteriaBuilderFactory cbf) {
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
    protected Class<Facility> getEntityClass() {
        return Facility.class;
    }

    @Override
    protected String getEntityAlias() {
        return "f";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<Facility> cb) {
        Set<String> roles = getLoggedInUserRole();
        if (roles.contains("SUPER_ADMIN")) {
            return;
        }

        cb.where("f.tenant.id").eq(TenantContextHolder.getCurrentTenant());
    }

    @Override
    public void applyFilters(CriteriaBuilder<Facility> cb, FacilityFilter filter) {
        if (filter.getId() != null) cb.where("f.id").eq(filter.getId());
        if (filter.getFacilityName() != null) cb.where("f.facilityName").eq(filter.getFacilityName());
        if (filter.getDescription() != null)
            cb.where("f.description").like().value("%" + filter.getDescription() + "%").noEscape();
        if (filter.getCapacity() != null) cb.where("f.capacity").eq(filter.getCapacity());
        if (filter.getLocation() != null) cb.where("f.location").eq(filter.getLocation());
        if (filter.getCloseTime() != null) cb.where("f.closeTime").le(filter.getCloseTime());
        if (filter.getOpenTime() != null) cb.where("f.openTime").ge(filter.getOpenTime());
        if (filter.getOpenForAll() != null) cb.where("f.openForAll").eq(filter.getOpenForAll());
        if (filter.getManager()!=null) cb.where("f.manager.id").eq(filter.getManager());
        if (filter.getTenantId()!=null) cb.where("f.tenant.id").eq(filter.getTenantId());
        if (filter.getIsActive()!=null) cb.where("f.isActive").eq(filter.getIsActive());


    }
}