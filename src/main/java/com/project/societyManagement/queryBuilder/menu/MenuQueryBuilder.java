package com.project.societyManagement.queryBuilder.menu;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.entity.Menu;
import com.project.societyManagement.entity.Role;
import com.project.societyManagement.entity.TenantRoleMenu;
import com.project.societyManagement.entity.User;
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
public class MenuQueryBuilder extends AbstractFilterableQueryBuilder<Menu, MenuFilter> {

    MenuQueryBuilder(EntityManager entityManager , CriteriaBuilderFactory cbf){
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
    protected Class<Menu> getEntityClass() {
        return Menu.class;
    }

    @Override
    protected String getEntityAlias() {
        return "m";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<Menu> cb){
        Set<String> roles = getLoggedInUserRole();
//        if(roles.contains("SUPER_ADMIN")){
//            cb.where("m.id").eq(0);
//            return;
//        }
        if (roles.contains("ADMIN")){
            return ;
        }

        User user = getCurrentUser();

        Set<Long> roleIds = user.getRoles().stream().map(Role::getId).collect(Collectors.toSet());

            cb.where("m.id").in().from(TenantRoleMenu.class,"trm")
                    .select("trm.menu.id")
                    .where("trm.tenantRoles.tenant.id").eq(TenantContextHolder.getCurrentTenant())
                    .where("trm.tenantRoles.role.id").in(roleIds)
                    .where("trm.isActive").eq(true).end();

    }

    @Override
    public void applyFilters(CriteriaBuilder<Menu> cb,MenuFilter filter){
        if(filter.getId()!=null) cb.where("m.id").eq(filter.getId());
        if(filter.getName() != null) cb.where("upper(m.menuName)").eq(filter.getName().toUpperCase());
        if(filter.getDescription() != null) cb.where("m.description").like().value("%"+filter.getDescription()+"%").noEscape();
        if(filter.getIsActive()!=null) cb.where("m.isActive").eq(filter.getIsActive());}
}
