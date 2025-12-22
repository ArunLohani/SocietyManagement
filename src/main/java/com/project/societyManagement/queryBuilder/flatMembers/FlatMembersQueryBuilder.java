package com.project.societyManagement.queryBuilder.flatMembers;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.entity.*;
import com.project.societyManagement.entity.types.FlatCategory;
import com.project.societyManagement.entity.types.FlatMembershipType;
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
public class FlatMembersQueryBuilder extends AbstractFilterableQueryBuilder<FlatMember, FlatMembersFilter> {

    FlatMembersQueryBuilder(EntityManager entityManager , CriteriaBuilderFactory cbf){
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
    protected Class<FlatMember> getEntityClass() {
        return FlatMember.class;
    }

    @Override
    protected String getEntityAlias() {
        return "fm";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<FlatMember> cb){
        Set<String> roles = getLoggedInUserRole();
        cb.where("fm.flat.tenant.id").eq(TenantContextHolder.getCurrentTenant());
    }

    @Override
    public void applyFilters(CriteriaBuilder<FlatMember> cb,FlatMembersFilter filter){
        if(filter.getId()!=null) cb.where("fm.id").eq(filter.getId());
        if(filter.getFlat() != null) cb.where("fm.flat.id").eq(filter.getFlat());
        if(filter.getUser() != null) cb.where("fm.user.id").eq(filter.getUser());
        if(filter.getType() != null) cb.where("fm.type").eq(FlatMembershipType.valueOf(filter.getType()));
        if(filter.getIsActive()!=null) cb.where("fm.isActive").eq(filter.getIsActive());}
}
