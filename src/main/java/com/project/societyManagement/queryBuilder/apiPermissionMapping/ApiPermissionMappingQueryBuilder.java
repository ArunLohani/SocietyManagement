package com.project.societyManagement.queryBuilder.apiPermissionMapping;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.societyManagement.entity.*;
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
public class ApiPermissionMappingQueryBuilder extends AbstractFilterableQueryBuilder<ApiPermissionMapping, ApiPermissionMappingFilter> {


    ApiPermissionMappingQueryBuilder(EntityManager entityManager , CriteriaBuilderFactory cbf){
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
    protected Class<ApiPermissionMapping> getEntityClass() {
        return ApiPermissionMapping.class;
    }

    @Override
    protected String getEntityAlias() {
        return "apm";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<ApiPermissionMapping> cb){


    }

    @Override
    public void applyFilters(CriteriaBuilder<ApiPermissionMapping> cb,ApiPermissionMappingFilter filter){
        if(filter.getId()!=null) cb.where("apm.id").eq(filter.getId());
        if(filter.getApi() != null) cb.where("apm.api").eq(filter.getApi());
        if(filter.getIsActive()!=null) cb.where("apm.isActive").eq(filter.getIsActive());}


}
