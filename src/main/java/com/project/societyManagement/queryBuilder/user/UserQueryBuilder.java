package com.project.societyManagement.queryBuilder.user;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.societyManagement.entity.Role;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.queryBuilder.core.AbstractFilterableQueryBuilder;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j
@Component
public class UserQueryBuilder extends AbstractFilterableQueryBuilder<User,UserFilter> {

    UserQueryBuilder(EntityManager entityManager , CriteriaBuilderFactory cbf){
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
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    protected String getEntityAlias() {
        return "u";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<User> cb){
        Set<String> roles = getLoggedInUserRole();
        if (roles.contains("ADMIN")){
            return ;
        }
        if (roles.contains("OWNER")){
             cb.where("u.tenant.id").eq(getLoggedInUserTenantId());
             return;
        }
        if (roles.contains("TENANT")){
            cb.where("u.tenant.id").eq(getLoggedInUserTenantId());
            return;
        }
    }

    @Override
    public void applyFilters(CriteriaBuilder<User> cb,UserFilter filter){
        if(filter.getUserId()!=null) cb.where("u.id").eq(filter.getUserId());
        if(filter.getName() != null) cb.where("u.name").like().value("%"+filter.getName()+"%").noEscape();
        if(filter.getEmail() != null) cb.where("u.email").like().value("%"+filter.getEmail()+"%").noEscape();
    }

}
