package com.project.societyManagement.queryBuilder.core;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.InsertCriteriaBuilder;
import com.blazebit.persistence.PagedList;
import com.project.societyManagement.exception.UserNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
@Slf4j
public abstract class AbstractFilterableQueryBuilder<T,F> implements BaseQueryBuilder<T,F> {

    protected final EntityManager entityManager;
    protected final CriteriaBuilderFactory cbf;

    public AbstractFilterableQueryBuilder(EntityManager entityManager,CriteriaBuilderFactory cbf){
        this.entityManager = entityManager;
        this.cbf = cbf;
    }

    @Override
    public T findById(F filter) throws UserNotFoundException {
        CriteriaBuilder<T> cb = buildSafeCriteria(filter);
        try {
            return cb.getSingleResult();
        } catch (NoResultException e) {
            throw new UserNotFoundException("Entity not found for the provided criteria.");
        }
    }

    @Override
    public List<T> search(F filter) {
        CriteriaBuilder<T> cb = buildSafeCriteria(filter);
        return cb.getResultList();
    }

    @Override
    public Page<T> searchPaginated(F filter , Pageable pageable){
        CriteriaBuilder<T> cb = buildSafeCriteria(filter);
        cb.orderByAsc(getDefaultOrderProperty());
        int pageNumber = pageable!=null? pageable.getPageNumber() : 0;
        int pageSize = pageable!=null?pageable.getPageSize() : 6;
        PagedList<T> resultList = cb.page(pageNumber,pageSize)
                .withInlineCountQuery(false)
                .getResultList();
        return new PageImpl<>(resultList , pageable!=null? pageable : PageRequest.of(pageNumber,pageSize),resultList.getTotalSize());
    }
    private CriteriaBuilder<T> buildSafeCriteria(F filter){
        CriteriaBuilder<T> cb = cbf.create(entityManager,getEntityClass())
                .from(getEntityClass(),getEntityAlias());
        applyAuthorization(cb);
        applyFilters(cb,filter);
        return cb;
    }

    private InsertCriteriaBuilder<T> buildInsertCriteriaBuilder(T entity){
        InsertCriteriaBuilder<T> icb = cbf.insert(entityManager,getEntityClass())
                .from(getEntityClass(),getEntityAlias());
        return icb;
    }
    protected abstract Class<T> getEntityClass();
    protected abstract String getEntityAlias();
    protected String getDefaultOrderProperty(){
        return getEntityAlias() + ".id";
    }
    protected void applyFilters(CriteriaBuilder<T>cb , F filter){};
    protected void applyAuthorization(CriteriaBuilder<T>cb){};



}
