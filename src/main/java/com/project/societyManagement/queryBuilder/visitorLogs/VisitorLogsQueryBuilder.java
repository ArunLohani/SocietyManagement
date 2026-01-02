package com.project.societyManagement.queryBuilder.visitorLogs;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.entity.*;
import com.project.societyManagement.entity.types.VisitorStatus;
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
public class VisitorLogsQueryBuilder extends AbstractFilterableQueryBuilder<VisitorLog, VisitorLogsFilter> {

    public VisitorLogsQueryBuilder(EntityManager entityManager, CriteriaBuilderFactory cbf) {
        super(entityManager, cbf);
    }

    /**
     * Get current authenticated user
     * @return User or null if not authenticated
     */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return (User) auth.getPrincipal();
    }

    /**
     * Get roles of logged-in user
     * @return Set of role names or empty set if no user
     */
    public Set<String> getLoggedInUserRole() {
        User user = getCurrentUser();
        if (user == null) {
            return Collections.emptySet();
        }
        return user.getRoles().stream()
                .map(Role::getRole)
                .collect(Collectors.toSet());
    }

    /**
     * Get logged-in user ID
     * @return User ID
     * @throws IllegalStateException if no authenticated user
     */
    public Long getLoggedInUserId() {
        User user = getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("No authenticated user found");
        }
        return user.getId();
    }

    /**
     * Get logged-in user's tenant ID
     * @return Tenant ID
     * @throws IllegalStateException if no authenticated user or tenant
     */
    public Long getLoggedInUserTenantId() {
        User user = getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("No authenticated user found");
        }
        if (user.getTenant() == null) {
            throw new IllegalStateException("User has no associated tenant");
        }
        log.info("Tenant Id : {}", user.getTenant().getId());
        return user.getTenant().getId();
    }

    @Override
    protected Class<VisitorLog> getEntityClass() {
        return VisitorLog.class;
    }

    @Override
    protected String getEntityAlias() {
        return "vl";
    }

    /**
     * Apply authorization based on user role
     * - ADMIN/GUARD: Can see all visitor requests in their tenant
     * - Regular User: Can only see their own visitor requests
     */
    @Override
    public void applyAuthorization(CriteriaBuilder<VisitorLog> cb) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        Set<String> roles = getLoggedInUserRole();
        if (roles.contains("ADMIN") || roles.contains("GUARD")) {
            // Admin/Guard can see all visitor requests in their tenant
            cb.where("vl.visitorRequest.flat.tenant.id").eq(TenantContextHolder.getCurrentTenant());
            log.debug("Admin/Guard authorization applied for tenant: {}", TenantContextHolder.getCurrentTenant());
            return;
        }

        cb.whereExists()
                .from(FlatMember.class, "fm")
                .where("fm.flat.id").eqExpression("vl.visitorRequest.flat.id")  // Changed mp to vr
                .where("fm.user.id").eq(currentUser.getId())
                .where("fm.isActive").eq(true)
                .end();
    }

    /**
     * Apply filters to the query with range support for all time-related fields
     */
    @Override
    public void applyFilters(CriteriaBuilder<VisitorLog> cb, VisitorLogsFilter filter) {

        // ID filter
        if (filter.getId() != null) {
            cb.where("vl.id").eq(filter.getId());
        }

        //VerifiedBy Filter
        if (filter.getVerifiedBy()!=null){
            cb.where("vl.verifiedBy.id").eq(filter.getVerifiedBy());
        }

        // EntryTime In range filters
        applyDateTimeRangeFilter(cb, "vl.entryTime", filter.getEntryTimeFrom(), filter.getEntryTimeTo());

        //ExitTime In range filters
        applyDateTimeRangeFilter(cb, "vl.exitTime", filter.getExitTimeFrom(), filter.getExitTimeTo());


        // Active filter (soft delete)
        if (filter.getIsActive() != null) {
            cb.where("vl.isActive").eq(filter.getIsActive());
        }

        // Apply sorting
        if (filter.getSortFilter() != null) {
            applySorting(cb, filter.getSortFilter().getProperty(), filter.getSortFilter().getAsc());
        }
    }

    /**
     * Helper method to apply date/time range filters
     * @param cb CriteriaBuilder
     * @param fieldPath Field path (e.g., "vr.expectedIn")
     * @param from Start of range (inclusive)
     * @param to End of range (inclusive)
     */
    private void applyDateTimeRangeFilter(CriteriaBuilder<VisitorLog> cb, String fieldPath,
                                          java.time.LocalDateTime from, java.time.LocalDateTime to) {
        if (from != null) {
            cb.where(fieldPath).ge(from);
            log.debug("Applied {} >= {}", fieldPath, from);
        }
        if (to != null) {
            cb.where(fieldPath).le(to);
            log.debug("Applied {} <= {}", fieldPath, to);
        }
    }
}