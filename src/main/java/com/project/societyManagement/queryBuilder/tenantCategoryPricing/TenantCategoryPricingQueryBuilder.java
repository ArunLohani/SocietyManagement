package com.project.societyManagement.queryBuilder.tenantCategoryPricing;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.entity.*;
import com.project.societyManagement.entity.types.FlatCategory;
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
public class TenantCategoryPricingQueryBuilder extends AbstractFilterableQueryBuilder<TenantCategoryPricing, TenantCategoryPricingFilter> {

    TenantCategoryPricingQueryBuilder(EntityManager entityManager , CriteriaBuilderFactory cbf){
        super(entityManager,cbf);
    }

    private User getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth==null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken){
            return null;
        }
        return (User) auth.getPrincipal();
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
    protected Class<TenantCategoryPricing> getEntityClass() {
        return TenantCategoryPricing.class;
    }

    @Override
    protected String getEntityAlias() {
        return "tcp";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<TenantCategoryPricing> cb){
        cb.where("tcp.tenant.id").eq(TenantContextHolder.getCurrentTenant());
    }

    @Override
    public void applyFilters(CriteriaBuilder<TenantCategoryPricing> cb,TenantCategoryPricingFilter filter){
        if(filter.getId()!=null) cb.where("tcp.id").eq(filter.getId());
        if(filter.getTenant()!=null) cb.where("tcp.tenant.id").eq(filter.getTenant());
        if(filter.getCategory()!=null) cb.where("tcp.category").eq(FlatCategory.valueOf(filter.getCategory()));
        if(filter.getMonthlyFee()!=null) cb.where("tcp.getMonthlyFee").eq(filter.getMonthlyFee());
        if(filter.getIsActive()!=null) cb.where("tcp.isActive").eq(filter.getIsActive());}
}
