package com.project.societyManagement.queryBuilder.flat;

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
public class FlatQueryBuilder extends AbstractFilterableQueryBuilder<Flat, FlatFilter> {

    FlatQueryBuilder(EntityManager entityManager , CriteriaBuilderFactory cbf){
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
    protected Class<Flat> getEntityClass() {
        return Flat.class;
    }

    @Override
    protected String getEntityAlias() {
        return "f";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<Flat> cb){
        Set<String> roles = getLoggedInUserRole();
        cb.where("f.tenant.id").eq(TenantContextHolder.getCurrentTenant());
    }

    @Override
    public void applyFilters(CriteriaBuilder<Flat> cb,FlatFilter filter){
        if(filter.getId()!=null) cb.where("f.id").eq(filter.getId());
        if(filter.getBlock() != null) cb.where("f.block").eq(filter.getBlock());
        if(filter.getNumber() != null) cb.where("f.number").eq(filter.getNumber());
        if(filter.getCategory() != null) cb.where("f.category").eq(FlatCategory.valueOf(filter.getBlock()));
        if(filter.getTenant() != null) cb.where("f.tenant.id").eq(filter.getTenant());
        if(filter.getFloor() != null) cb.where("f.floor").eq(filter.getFloor());
        if(filter.getSqFt() != null) cb.where("f.sqFt").eq(filter.getSqFt());
        if (filter.getMember() != null) {
            cb.innerJoin("f.members", "m")
                    .where("m.user.id").eq(filter.getMember())
                    .where("m.isActive").eq(true);
        }
        if(filter.getIsActive()!=null) cb.where("f.isActive").eq(filter.getIsActive());}
}
