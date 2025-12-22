package com.project.societyManagement.queryBuilder.notice;

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
public class NoticeQueryBuilder extends AbstractFilterableQueryBuilder<Notice, NoticeFilter> {

    NoticeQueryBuilder(EntityManager entityManager , CriteriaBuilderFactory cbf){
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
    protected Class<Notice> getEntityClass() {
        return Notice.class;
    }

    @Override
    protected String getEntityAlias() {
        return "n";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<Notice> cb){
        Set<String> roles = getLoggedInUserRole();

        if (roles.contains("ADMIN")){
            cb.where("n.tenant.id").eq(TenantContextHolder.getCurrentTenant());
               return;
        }
        cb.where("n.tenant.id").eq(TenantContextHolder.getCurrentTenant())
                .where("n.isPublic").eq(true);

    }

    @Override
    public void applyFilters(CriteriaBuilder<Notice> cb,NoticeFilter filter){
        if(filter.getId()!=null) cb.where("n.id").eq(filter.getId());
        if(filter.getTitle() != null) cb.where("n.title").eq(filter.getTitle());
        if(filter.getMessage() != null) cb.where("n.message").eq(filter.getMessage());
        if(filter.getIsActive() != null) cb.where("n.isActive").eq(filter.getIsActive());
        if(filter.getIsPublic() != null) cb.where("n.isPublic").eq(filter.getIsPublic());
        if (filter.getIsExpired()!=null)cb.where("n.isExpired").eq(filter.getIsExpired());
        if (filter.getTenantId()!=null) cb.where("n.tenant.id").eq(filter.getTenantId());
        if (filter.getCategory()!=null) cb.where("n.category").eq(filter.getCategory());
        if (filter.getSortFilter() != null){
            applySorting(cb,filter.getSortFilter().getProperty(),filter.getSortFilter().getAsc());
        }
    }
}
