package com.project.societyManagement.queryBuilder.visitorRequest;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.entity.FlatMember;
import com.project.societyManagement.entity.Role;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.VisitorRequest;
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
public class VisitorRequestQueryBuilder extends AbstractFilterableQueryBuilder<VisitorRequest, VisitorRequestFilter> {

    public VisitorRequestQueryBuilder(EntityManager entityManager, CriteriaBuilderFactory cbf) {
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
    protected Class<VisitorRequest> getEntityClass() {
        return VisitorRequest.class;
    }

    @Override
    protected String getEntityAlias() {
        return "vr";
    }

    /**
     * Apply authorization based on user role
     * - ADMIN/GUARD: Can see all visitor requests in their tenant
     * - Regular User: Can only see their own visitor requests
     */
    @Override
    public void applyAuthorization(CriteriaBuilder<VisitorRequest> cb) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        Set<String> roles = getLoggedInUserRole();
        if (roles.contains("ADMIN") || roles.contains("GUARD")) {
            // Admin/Guard can see all visitor requests in their tenant
            cb.where("vr.flat.tenant.id").eq(TenantContextHolder.getCurrentTenant());
            log.debug("Admin/Guard authorization applied for tenant: {}", TenantContextHolder.getCurrentTenant());
            return;
        }

        cb.whereExists()
                .from(FlatMember.class, "fm")
                .where("fm.flat.id").eqExpression("vr.flat.id")  // Changed mp to vr
                .where("fm.user.id").eq(currentUser.getId())
                .where("fm.isActive").eq(true)
                .end();
    }

    /**
     * Apply filters to the query with range support for all time-related fields
     */
    @Override
    public void applyFilters(CriteriaBuilder<VisitorRequest> cb, VisitorRequestFilter filter) {

        // ID filter
        if (filter.getId() != null) {
            cb.where("vr.id").eq(filter.getId());
        }

        // Flat filter
        if (filter.getFlat() != null) {
            cb.where("vr.flat.id").eq(filter.getFlat());
        }

        // User filter
        if (filter.getRequestedBy() != null) {
            cb.where("vr.requestedBy.id").eq(filter.getRequestedBy());
        }

        // Status filter
        if (filter.getStatus() != null) {
            cb.where("vr.status").eq(VisitorStatus.valueOf(filter.getStatus()));
        }

        // Visitor name filter (LIKE search)
        if (filter.getVisitorName() != null) {
            cb.where("vr.visitorName").like().value("%" + filter.getVisitorName() + "%").noEscape();
        }

        // Visitor phone filter (LIKE search)
        if (filter.getVisitorPhone() != null) {
            cb.where("vr.visitorPhone").like().value("%" + filter.getVisitorPhone() + "%").noEscape();
        }

        // Visitor phone email (LIKE search)
        if (filter.getVisitorEmail() != null) {
            cb.where("vr.visitorEmail").like().value("%" + filter.getVisitorEmail() + "%").noEscape();
        }

        // Purpose filter (LIKE search)
        if (filter.getPurpose() != null) {
            cb.where("vr.purpose").like().value("%" + filter.getPurpose() + "%").noEscape();
        }

        // Expected In range filters
        applyDateTimeRangeFilter(cb, "vr.expectedIn", filter.getExpectedInFrom(), filter.getExpectedInTo());

        // Expected Out range filters
        applyDateTimeRangeFilter(cb, "vr.expectedOut", filter.getExpectedOutFrom(), filter.getExpectedOutTo());

        // Approved At range filters
        applyDateTimeRangeFilter(cb, "vr.approvedAt", filter.getApprovedAtFrom(), filter.getApprovedAtTo());

        // Entered At range filters
        applyDateTimeRangeFilter(cb, "vr.enteredAt", filter.getEnteredAtFrom(), filter.getEnteredAtTo());

        // Exited At range filters
        applyDateTimeRangeFilter(cb, "vr.exitedAt", filter.getExitedAtFrom(), filter.getExitedAtTo());

        // Created At range filters
        applyDateTimeRangeFilter(cb, "vr.createdAt", filter.getCreatedAtFrom(), filter.getCreatedAtTo());

        // Updated At range filters
        applyDateTimeRangeFilter(cb, "vr.updatedAt", filter.getUpdatedAtFrom(), filter.getUpdatedAtTo());

        // Active filter (soft delete)
        if (filter.getIsActive() != null) {
            cb.where("vr.isActive").eq(filter.getIsActive());
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
    private void applyDateTimeRangeFilter(CriteriaBuilder<VisitorRequest> cb, String fieldPath,
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