package com.project.societyManagement.queryBuilder.tenant;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.entity.Role;
import com.project.societyManagement.entity.Tenant;
import com.project.societyManagement.entity.TenantRoles;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.queryBuilder.core.AbstractFilterableQueryBuilder;
import com.project.societyManagement.queryBuilder.role.RoleFilter;
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
public class TenantQueryBuilder extends AbstractFilterableQueryBuilder<Tenant, TenantFilter> {

    TenantQueryBuilder(EntityManager entityManager , CriteriaBuilderFactory cbf){
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
    protected Class<Tenant> getEntityClass() {
        return Tenant.class;
    }

    @Override
    protected String getEntityAlias() {
        return "t";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<Tenant> cb){
        Set<String> roles = getLoggedInUserRole();
        if(roles.contains("SUPER_ADMIN")){
            return;
        }
        User user = getCurrentUser();
        Set<Long> roleIds = user.getRoles().stream().map(Role::getId).collect(Collectors.toSet());
        if(roles.contains("ADMIN")){
            cb.where("t.id").eq(TenantContextHolder.getCurrentTenant());
            return;
        }
        if (roles.contains("OWNER")) {
            cb.where("t.id").eq(TenantContextHolder.getCurrentTenant());
            return;
        }
        if (roles.contains("TENANT")){
            cb.where("t.id").eq(TenantContextHolder.getCurrentTenant());
            return;
        }
    }

    @Override
    public void applyFilters(CriteriaBuilder<Tenant> cb,TenantFilter filter){
        if(filter.getId()!=null) cb.where("t.id").eq(filter.getId());
        if(filter.getName() != null) cb.where("t.name").eq(filter.getName());
        if (filter.getIsActive()!=null) cb.where("t.isActive").eq(filter.getIsActive());

    }


}