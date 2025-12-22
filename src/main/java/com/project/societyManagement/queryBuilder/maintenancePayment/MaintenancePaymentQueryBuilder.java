
package com.project.societyManagement.queryBuilder.maintenancePayment;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.entity.*;
import com.project.societyManagement.entity.types.PaymentStatus;
import com.project.societyManagement.queryBuilder.core.AbstractFilterableQueryBuilder;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MaintenancePaymentQueryBuilder extends AbstractFilterableQueryBuilder<MaintenancePayment, MaintenancePaymentFilter> {

    public MaintenancePaymentQueryBuilder(EntityManager entityManager, CriteriaBuilderFactory cbf) {
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
    protected Class<MaintenancePayment> getEntityClass() {
        return MaintenancePayment.class;
    }

    @Override
    protected String getEntityAlias() {
        return "mp";
    }

    /**
     * Apply authorization based on user role
     * - ADMIN: Can see all payments in their tenant
     * - Regular User: Can only see their own payments
     */
    @Override
    public void applyAuthorization(CriteriaBuilder<MaintenancePayment> cb) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        Set<String> roles = getLoggedInUserRole();
        if (roles.contains("ADMIN")) {
            // Admin can see all payments in their tenant
            cb.where("mp.flat.tenant.id").eq(TenantContextHolder.getCurrentTenant());
            log.debug("Admin authorization applied for tenant: {}", TenantContextHolder.getCurrentTenant());
            return;
        }

        cb.where("mp.user.id").eq(currentUser.getId());

//        cb.whereExists()
//                .from(FlatMember.class, "fm")
//                .where("fm.flat.id").eqExpression("mp.flat.id")
//                .where("fm.user.id").eq(currentUser.getId())
//                .where("fm.isActive").eq(true) // Only active memberships
//                .end();
        // Regular users can only see their own payments
//        cb.where("mp.user.id").eq(currentUser.getId());
//        log.debug("User authorization applied for user: {}", currentUser.getId());
    }

    /**
     * Apply filters to the query
     */
    @Override
    public void applyFilters(CriteriaBuilder<MaintenancePayment> cb, MaintenancePaymentFilter filter) {

        // ID filter
        if (filter.getId() != null) {
            cb.where("mp.id").eq(filter.getId());
        }

        // Flat filter
        if (filter.getFlat() != null) {
            cb.where("mp.flat.id").eq(filter.getFlat());
        }

        // User filter
        if (filter.getUser() != null) {
            cb.where("mp.user.id").eq(filter.getUser());
        }

        // ========== BILLING START DATE FILTERS ==========

        // Exact billing start date
        if (filter.getBillingStartDate() != null) {
            cb.where("mp.billingStartDate").eq(filter.getBillingStartDate());
        }

        // Billing start date range
        if (filter.getBillingStartDateFrom() != null) {
            cb.where("mp.billingStartDate").ge(filter.getBillingStartDateFrom());
        }

        if (filter.getBillingStartDateTo() != null) {
            cb.where("mp.billingStartDate").le(filter.getBillingStartDateTo());
        }

        // ========== BILLING END DATE FILTERS ==========

        // Exact billing end date
        if (filter.getBillingEndDate() != null) {
            cb.where("mp.billingEndDate").eq(filter.getBillingEndDate());
        }

        // Billing end date range
        if (filter.getBillingEndDateFrom() != null) {
            cb.where("mp.billingEndDate").ge(filter.getBillingEndDateFrom());
        }

        if (filter.getBillingEndDateTo() != null) {
            cb.where("mp.billingEndDate").le(filter.getBillingEndDateTo());
        }

        // ========== PAYMENT DATE FILTERS ==========

        // Exact payment date
        if (filter.getPaymentDate() != null) {
            cb.where("mp.paymentDate").eq(filter.getPaymentDate());
        }

        // Payment date range
        if (filter.getPaymentDateFrom() != null) {
            cb.where("mp.paymentDate").ge(filter.getPaymentDateFrom());
        }

        if (filter.getPaymentDateTo() != null) {
            cb.where("mp.paymentDate").le(filter.getPaymentDateTo());
        }

        // ========== SPECIAL DATE FILTERS ==========

        // Active payments on a specific date (payment is active if billingEndDate >= date)
        if (filter.getActivePaymentDate() != null) {
            cb.where("mp.billingEndDate").ge(filter.getActivePaymentDate());
            cb.where("mp.billingStartDate").le(filter.getActivePaymentDate());
        }

        // Expired payments before a date (billingEndDate < date)
        if (filter.getExpiredBeforeDate() != null) {
            cb.where("mp.billingEndDate").lt(filter.getExpiredBeforeDate());
        }

        // Payments expiring soon (within specified days, default 7)
        if (filter.getIsExpiringSoon() != null && filter.getIsExpiringSoon()) {
            int days = filter.getDaysUntilExpiry() != null ? filter.getDaysUntilExpiry() : 7;
            LocalDate today = LocalDate.now();
            LocalDate futureDate = today.plusDays(days);

            // Payments that expire between today and futureDate
            cb.where("mp.billingEndDate").ge(today);
            cb.where("mp.billingEndDate").le(futureDate);
        }

        // ========== BILLING CYCLE AND AMOUNT FILTERS ==========

        if (filter.getBillingCycle() != null) {
            cb.where("mp.billingCycle").eq(filter.getBillingCycle());
        }

        if (filter.getMonthsCovered() != null) {
            cb.where("mp.monthsCovered").eq(filter.getMonthsCovered());
        }

        if (filter.getMonthlyFee() != null) {
            cb.where("mp.monthlyFee").eq(filter.getMonthlyFee());
        }

        // Final amount filters
        if (filter.getFinalAmount() != null) {
            cb.where("mp.finalAmount").eq(filter.getFinalAmount());
        }

        if (filter.getFinalAmountMin() != null) {
            cb.where("mp.finalAmount").ge(filter.getFinalAmountMin());
        }

        if (filter.getFinalAmountMax() != null) {
            cb.where("mp.finalAmount").le(filter.getFinalAmountMax());
        }

        // ========== PAYMENT STATUS AND DETAILS ==========

        if (filter.getStatus() != null) {
            cb.where("mp.status").eq(PaymentStatus.valueOf(filter.getStatus()));
        }

        if (filter.getTransactionId() != null) {
            cb.where("mp.transactionId").eq(filter.getTransactionId());
        }

        if (filter.getPaymentMethod() != null) {
            cb.where("mp.paymentMethod").eq(filter.getPaymentMethod());
        }

        if (filter.getPaymentGatewayResponse() != null) {
            cb.where("mp.paymentGatewayResponse").like().value("%" + filter.getPaymentGatewayResponse() + "%").noEscape();
        }

        if (filter.getReferenceNumber() != null) {
            cb.where("mp.referenceNumber").eq(filter.getReferenceNumber());
        }

        // Active filter (soft delete)
        if (filter.getIsActive() != null) {
            cb.where("mp.isActive").eq(filter.getIsActive());
        }

        if (filter.getSortFilter() != null){
            applySorting(cb,filter.getSortFilter().getProperty(),filter.getSortFilter().getAsc());
        }
    }
}