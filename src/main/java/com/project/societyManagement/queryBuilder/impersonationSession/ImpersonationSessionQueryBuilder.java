package com.project.societyManagement.queryBuilder.impersonationSession;

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
public class ImpersonationSessionQueryBuilder extends AbstractFilterableQueryBuilder<ImpersonationSession, ImpersonationSessionFilter> {

    public ImpersonationSessionQueryBuilder(EntityManager entityManager, CriteriaBuilderFactory cbf) {
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
    protected Class<ImpersonationSession> getEntityClass() {
        return ImpersonationSession.class;
    }

    @Override
    protected String getEntityAlias() {
        return "is";
    }

    /**
     * Apply authorization based on user role
     */
    @Override
    public void applyAuthorization(CriteriaBuilder<ImpersonationSession> cb) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        Set<String> roles = getLoggedInUserRole();
        cb.fetch("is.superAdmin");
            cb.whereOr()
                    .where("is.admin.id").eq(getCurrentUser().getId())
                    .where("is.superAdmin.id").eq(getCurrentUser().getId()).endOr();



    }

    /**
     * Apply filters to the query with range support for all time-related fields
     */
    @Override
    public void applyFilters(CriteriaBuilder<ImpersonationSession> cb, ImpersonationSessionFilter filter) {
        if (filter.getId() != null) {
            cb.where("is.id").eq(filter.getId());
        }

        if (filter.getAdmin() != null) {
            cb.where("is.admin.id").eq(filter.getAdmin());
        }

        if (filter.getSuperAdmin() != null) {
            cb.where("is.superAdmin.id").eq(filter.getSuperAdmin());
        }

        if (filter.getTicket() != null) {
            cb.where("is.ticket.id").eq(filter.getTicket());
        }

        applyDateTimeRangeFilter(cb, "is.expiresAt", filter.getExpiresAtFrom(), filter.getExpiresAtTo());
        applyDateTimeRangeFilter(cb, "is.endedAt", filter.getEndedAtFrom(), filter.getEndedAtTo());

        // NEW: Check if endedAt is null
        if (filter.getEndedAtIsNull() != null) {
            if (filter.getEndedAtIsNull()) {
                cb.where("is.endedAt").isNull();
            } else {
                cb.where("is.endedAt").isNotNull();
            }
        }

        if (filter.getIsActive() != null) {
            cb.where("is.isActive").eq(filter.getIsActive());
        }

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
    private void applyDateTimeRangeFilter(CriteriaBuilder<ImpersonationSession> cb, String fieldPath,
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