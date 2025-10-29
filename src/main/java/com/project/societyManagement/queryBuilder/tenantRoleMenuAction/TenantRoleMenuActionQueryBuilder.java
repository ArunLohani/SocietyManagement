package com.project.societyManagement.queryBuilder.tenantRoleMenuAction;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
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
public class TenantRoleMenuActionQueryBuilder extends AbstractFilterableQueryBuilder<TenantRoleMenuAction, TenantRoleMenuActionFilter> {

    TenantRoleMenuActionQueryBuilder(EntityManager entityManager , CriteriaBuilderFactory cbf){
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
    protected Class<TenantRoleMenuAction> getEntityClass() {
        return TenantRoleMenuAction.class;
    }

    @Override
    protected String getEntityAlias() {
        return "trma";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<TenantRoleMenuAction> cb){
        Set<String> roles = getLoggedInUserRole();
        if (roles.contains("ADMIN")){
            return ;
        }

//        User user = getCurrentUser();
//        Long tenantId = user.getTenant().getId();
//        Set<Long> roleIds = user.getRoles().stream().map(Role::getId).collect(Collectors.toSet());
//
//        cb.where("tr.tenant.id").eq(tenantId);

    }

    @Override
    public void applyFilters(CriteriaBuilder<TenantRoleMenuAction> cb,TenantRoleMenuActionFilter filter){
        if(filter.getId()!=null) cb.where("trma.id").eq(filter.getId());
        if(filter.getTenantRoleMenuId() != null) cb.where("trma.tenantRoleMenu.id").eq(filter.getTenantRoleMenuId());
        if(filter.getActionId() != null) cb.where("trma.action.id").eq(filter.getActionId());
    }
}
