package com.project.societyManagement.queryBuilder.supportTicket;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.entity.*;
import com.project.societyManagement.entity.types.TicketStatus;
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
public class SupportTicketQueryBuilder extends AbstractFilterableQueryBuilder<SupportTicket, SupportTicketFilter> {

    public SupportTicketQueryBuilder(EntityManager entityManager, CriteriaBuilderFactory cbf) {
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
    protected Class<SupportTicket> getEntityClass() {
        return SupportTicket.class;
    }

    @Override
    protected String getEntityAlias() {
        return "st";
    }

    /**
     * Apply authorization based on user role
     * - ADMIN/GUARD: Can see all visitor requests in their tenant
     * - Regular User: Can only see their own visitor requests
     */
    @Override
    public void applyAuthorization(CriteriaBuilder<SupportTicket> cb) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user found");
        }


        Set<String> roles = getLoggedInUserRole();
        if (roles.contains("ADMIN")) {
            cb.where("st.raisedBy.id").eq(getCurrentUser().getId());
            return;
        }

        if (roles.contains("SUPER_ADMIN")) return;

        cb.where("st.id").eq("0");

    }

    /**
     * Apply filters to the query with range support for all time-related fields
     */
    @Override
    public void applyFilters(CriteriaBuilder<SupportTicket> cb, SupportTicketFilter filter) {

        // ID filter
        if (filter.getId() != null) {
            cb.where("st.id").eq(filter.getId());
        }

        if (filter.getRaisedBy() != null) {
            cb.where("st.raisedBy.id").eq(filter.getRaisedBy());
        }

        if (filter.getTitle() != null) {
            cb.where("st.title").eq(filter.getTitle());
        }

        if (filter.getDescription() != null) {
            cb.where("st.description").eq(filter.getDescription());
        }

        if (filter.getStatus() != null) {
            cb.where("st.status").eq(TicketStatus.valueOf(filter.getStatus()));
        }

        // Active filter (soft delete)
        if (filter.getIsActive() != null) {
            cb.where("st.isActive").eq(filter.getIsActive());
        }

        // Apply sorting
        if (filter.getSortFilter() != null) {
            applySorting(cb, filter.getSortFilter().getProperty(), filter.getSortFilter().getAsc());
        }
    }


}