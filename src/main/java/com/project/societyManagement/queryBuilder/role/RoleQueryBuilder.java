package com.project.societyManagement.queryBuilder.role;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.societyManagement.config.TenantContextHolder;
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
public class RoleQueryBuilder extends AbstractFilterableQueryBuilder<Role, RoleFilter> {

    RoleQueryBuilder(EntityManager entityManager , CriteriaBuilderFactory cbf){
        super(entityManager,cbf);
    }

    private User getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth==null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken){
            return null;
        }
        Object principal = auth.getPrincipal();
        //Check if principal is actually User entity
        if(principal instanceof User){
            return (User) principal;
        }
        //During Oauth2 login, principal might be of Oauth2User
        return null;
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
    protected Class<Role> getEntityClass() {
        return Role.class;
    }

    @Override
    protected String getEntityAlias() {
        return "r";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<Role> cb){
            Set<String> roles = getLoggedInUserRole();
            if(roles.contains("SUPER_ADMIN")){
                return;
            }
        if (roles.contains("ADMIN")){
            cb
//                .where("r.role").notEq("ADMIN")
                    .where("r.role").notEq("SUPER_ADMIN");
            return;
        }
            User user = getCurrentUser();

        if(user!=null && user.getRoles() != null){

            Set<Long> roleIds = user.getRoles().stream().map(Role::getId).collect(Collectors.toSet());

            cb.where("r.id").in().from(TenantRoles.class,"tr")
                    .select("tr.role.id").where("tr.tenant.id").eq(TenantContextHolder.getCurrentTenant()).end();
        }

    }

    @Override
    public void applyFilters(CriteriaBuilder<Role> cb,RoleFilter filter){
        if(filter.getId()!=null) cb.where("r.id").eq(filter.getId());
        if(filter.getRole() != null) cb.where("r.role").eq(filter.getRole());
        if (filter.getIsActive()!=null) cb.where("r.isActive").eq(filter.getIsActive());

    }


}
