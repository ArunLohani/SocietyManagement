package com.project.societyManagement.queryBuilder.core;

import com.project.societyManagement.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BaseQueryBuilder <T , F> {

    T findById(F filter) throws UserNotFoundException;
    List<T> search(F filter);
    Page<T> searchPaginated(F filter , Pageable pageable);


}
