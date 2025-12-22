package com.project.societyManagement.queryBuilder.complaints;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.entity.*;
import com.project.societyManagement.entity.types.ComplaintStatus;
import com.project.societyManagement.entity.types.Priority;
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
public class ComplaintsQueryBuilder extends AbstractFilterableQueryBuilder<Complaints, ComplaintsFilter> {

    ComplaintsQueryBuilder(EntityManager entityManager , CriteriaBuilderFactory cbf){
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
    protected Class<Complaints> getEntityClass() {
        return Complaints.class;
    }

    @Override
    protected String getEntityAlias() {
        return "c";
    }

    @Override
    public void applyAuthorization(CriteriaBuilder<Complaints> cb){
        Set<String> roles = getLoggedInUserRole();
        if(roles.contains("SUPER_ADMIN")){
            return;
        }
        if (roles.contains("ADMIN")){
            cb.where("c.tenant.id").eq(TenantContextHolder.getCurrentTenant());
            return;
        }
        cb.where("c.tenant.id").eq(TenantContextHolder.getCurrentTenant())
                .whereOr()
                .where("c.raisedByUser.id").eq(getCurrentUser().getId())
                .where("c.assignedToUser.id").eq(getCurrentUser().getId()).endOr();
    }

    @Override
    public void applyFilters(CriteriaBuilder<Complaints> cb,ComplaintsFilter filter){
        if(filter.getId()!=null) cb.where("c.id").eq(filter.getId());
        if(filter.getTitle() != null) cb.where("c.title").eq(filter.getTitle());
        if(filter.getDescription() != null) cb.where("c.description").eq(filter.getDescription());
        if(filter.getStatus() != null) cb.where("c.status").eq(ComplaintStatus.valueOf(filter.getStatus()));
        if (filter.getRaisedByUser()!=null) cb.where("c.raisedByUser.id").eq(filter.getRaisedByUser());
        if (filter.getAssignedToUser()!=null) cb.where("c.assignedToUser.id").eq(filter.getAssignedToUser());
        if(filter.getIsActive() != null) cb.where("c.isActive").eq(filter.getIsActive());
        if (filter.getTenantId()!=null) cb.where("c.tenant.id").eq(filter.getTenantId());
        if (filter.getCategory()!=null) cb.where("c.category").eq(filter.getCategory());
        if (filter.getPriority()!=null) cb.where("c.priority").eq(Priority.valueOf(filter.getPriority()));
        if (filter.getSortFilter() != null){
            applySorting(cb,filter.getSortFilter().getProperty(),filter.getSortFilter().getAsc());
        }
    }
}
