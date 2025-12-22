package com.project.societyManagement.queryBuilder.facilityRegisteredUser;

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
public class FacilityRegisteredUserQueryBuilder extends AbstractFilterableQueryBuilder<FacilityRegisteredUser, FacilityRegisteredUserFilter> {

    FacilityRegisteredUserQueryBuilder(EntityManager entityManager, CriteriaBuilderFactory cbf) {
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
    protected Class<FacilityRegisteredUser> getEntityClass() {
        return FacilityRegisteredUser.class;
    }

    @Override
    protected String getEntityAlias() {
        return "fru";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<FacilityRegisteredUser> cb) {
        Set<String> roles = getLoggedInUserRole();
        if (roles.contains("SUPER_ADMIN")) {
            return;
        }


    }

    @Override
    public void applyFilters(CriteriaBuilder<FacilityRegisteredUser> cb, FacilityRegisteredUserFilter filter) {
        if (filter.getId() != null) cb.where("f.id").eq(filter.getId());
        if (filter.getFacility() != null) cb.where("f.facility.id").eq(filter.getFacility());
        if (filter.getUser() != null) cb.where("f.user.id").eq(filter.getUser());
        if (filter.getIsActive()!=null) cb.where("f.isActive").eq(filter.getIsActive());

    }
}