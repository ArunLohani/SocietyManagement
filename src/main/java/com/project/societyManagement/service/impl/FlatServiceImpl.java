package com.project.societyManagement.service.impl;

import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.dto.Flat.FlatCreationRequest;
import com.project.societyManagement.entity.Flat;
import com.project.societyManagement.entity.FlatMember;
import com.project.societyManagement.entity.Tenant;
import com.project.societyManagement.queryBuilder.flat.FlatFilter;
import com.project.societyManagement.queryBuilder.flat.FlatQueryBuilder;
import com.project.societyManagement.repository.FlatRepo;
import com.project.societyManagement.service.FlatService;
import com.project.societyManagement.service.TenantService;
import com.project.societyManagement.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlatServiceImpl implements FlatService {

    private final FlatQueryBuilder flatQueryBuilder;
    private final FlatRepo flatRepo;
    private final ModelMapper modelMapper;
    private final TenantService tenantService;
    private final ValidationUtil validationUtil;

    public Flat getFlatById(Long id){
        FlatFilter flatFilter = new FlatFilter();
        flatFilter.setId(id);
        return flatQueryBuilder.findById(flatFilter);
    }

    public Flat createFlat(FlatCreationRequest flatCreationRequest){
        validationUtil.validate(flatCreationRequest);
        Flat flat = modelMapper.map(flatCreationRequest,Flat.class);
        Tenant tenant = tenantService.findTenantById(TenantContextHolder.getCurrentTenant());
        flat.setTenant(tenant);
        return flatRepo.save(flat);
    }

    public Flat updateFlat(Long id , FlatCreationRequest flatCreationRequest){
        validationUtil.validate(flatCreationRequest);
        Flat flat = getFlatById(id);
        if (flatCreationRequest.getCategory()!=null){
            flat.setCategory(flatCreationRequest.getCategory());
        }
        if (flatCreationRequest.getNumber()!=null){
            flat.setNumber(flatCreationRequest.getNumber());
        }

        if (flatCreationRequest.getBlock()!=null){
            flat.setBlock(flatCreationRequest.getBlock());
        }
        if (flatCreationRequest.getSqFt()!=null){
            flat.setSqFt(flatCreationRequest.getSqFt());
        }
        if (flatCreationRequest.getFloor()!=null){
            flat.setFloor(flatCreationRequest.getFloor());
        }
        return flatRepo.save(flat);
    }

    public Flat deleteFlat(Long id){
        Flat flat = getFlatById(id);
        flat.setIsActive(false);
        return flatRepo.save(flat);
    }

    public Flat addMember(Long id,FlatMember member){
        Flat flat = getFlatById(id);
       List<FlatMember> memberList = flat.getMembers();
       memberList.add(member);
       flat.setMembers(memberList);
        return flatRepo.save(flat);
    }

    public Flat removeMember(Long id ,FlatMember member){
        Flat flat = getFlatById(id);
        List<FlatMember> memberList = flat.getMembers();
        memberList.remove(member);
        flat.setMembers(memberList);
        return flatRepo.save(flat);
    }

    public List<Flat> searchFlat(FlatFilter filter){
        return flatQueryBuilder.search(filter);
    }
    public Page<Flat> searchFlatPaginated(FlatFilter filter, Pageable pageable){
        return flatQueryBuilder.searchPaginated(filter,pageable);
    }

}
