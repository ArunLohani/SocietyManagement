package com.project.societyManagement.service;

import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.dto.Flat.FlatCreationRequest;
import com.project.societyManagement.entity.Flat;
import com.project.societyManagement.entity.FlatMember;
import com.project.societyManagement.entity.Tenant;
import com.project.societyManagement.queryBuilder.flat.FlatFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FlatService {
    public Flat createFlat(FlatCreationRequest flatCreationRequest);
    public Flat updateFlat(Long id , FlatCreationRequest flatCreationRequest);
    public Flat deleteFlat(Long id);
    public Flat addMember(Long id, FlatMember member);
    public Flat removeMember(Long id ,FlatMember member);
    public Flat getFlatById(Long id);
    public List<Flat> searchFlat(FlatFilter filter);
    public Page<Flat> searchFlatPaginated(FlatFilter filter, Pageable pageable);
}
