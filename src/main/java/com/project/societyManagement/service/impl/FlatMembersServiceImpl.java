package com.project.societyManagement.service.impl;

import com.project.societyManagement.annotations.Auditing;
import com.project.societyManagement.dto.FlatMember.FlatMemberAddRequest;
import com.project.societyManagement.entity.Flat;
import com.project.societyManagement.entity.FlatMember;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.FlatMembershipType;
import com.project.societyManagement.queryBuilder.flatMembers.FlatMembersFilter;
import com.project.societyManagement.queryBuilder.flatMembers.FlatMembersQueryBuilder;
import com.project.societyManagement.repository.FlatMembersRepo;
import com.project.societyManagement.service.FlatMembersService;
import com.project.societyManagement.service.FlatService;
import com.project.societyManagement.service.UserService;
import com.project.societyManagement.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlatMembersServiceImpl implements FlatMembersService {

    private final FlatMembersQueryBuilder flatMembersQueryBuilder;
    private final ModelMapper modelMapper;
    private final FlatMembersRepo flatMembersRepo;
    private final FlatService flatService;
    private final UserService userService;
    private final ValidationUtil validationUtil;

    @Auditing(entity = "FlatMember",action = "READ")
    public FlatMember flatMemberExists(Long flatId , Long userId){
        FlatMembersFilter filter = new FlatMembersFilter();
        filter.setFlat(flatId);
        filter.setUser(userId);
        List<FlatMember> flatMembers = flatMembersQueryBuilder.search(filter);
        if (flatMembers.size() == 0){
            return null;
        }
        return flatMembers.get(0);
    }
    @Auditing(entity = "FlatMember",action = "READ")
    public FlatMember flatMemberExistsIncludeInactive(Long flatId , Long userId){
        FlatMembersFilter filter = new FlatMembersFilter();
        filter.setFlat(flatId);
        filter.setUser(userId);
        filter.setIsActive(null);
        List<FlatMember> flatMembers = flatMembersQueryBuilder.search(filter);
        if (flatMembers.size() == 0){
            return null;
        }
        return flatMembers.get(0);
    }
    @Auditing(entity = "FlatMember",action = "READ")

    public FlatMember findFlatMemberById(Long id){
        FlatMembersFilter filter = new FlatMembersFilter();
        filter.setId(id);
        return flatMembersQueryBuilder.findById(filter);
    }
    @Auditing(entity = "FlatMember",action = "EDIT")
    public FlatMember addOwnerToFlat(Long flatId , Long userId){
      FlatMember flatMember = flatMemberExistsIncludeInactive(flatId,userId);
      if (flatMember!=null){
          flatMember.setType(FlatMembershipType.OWNER);
          flatMember.setIsActive(true);
      }
        else {
          Flat flat = flatService.getFlatById(flatId);
          User user = userService.findUserById(userId);
          flatMember = FlatMember.builder().flat(flat).user(user).type(FlatMembershipType.OWNER).isActive(true).build();
//         flatService.addMember(flatId,flatMember);
      }
        return flatMembersRepo.save(flatMember);
    }

    @Auditing(entity = "FlatMember",action = "EDIT")
    public FlatMember addMemberToFlat(FlatMemberAddRequest flatMemberAddRequest){
        validationUtil.validate(flatMemberAddRequest);
        FlatMember flatMember = flatMemberExistsIncludeInactive(flatMemberAddRequest.getFlatId(),flatMemberAddRequest.getUserId());
        if (flatMember!=null){
            flatMember.setType(flatMemberAddRequest.getType());
            flatMember.setIsActive(true);
        }
        else {
            Flat flat = flatService.getFlatById(flatMemberAddRequest.getFlatId());
            User user = userService.findUserById(flatMemberAddRequest.getUserId());
            flatMember = FlatMember.builder().flat(flat).user(user).type(flatMemberAddRequest.getType()).isActive(true).build();
//            flatService.addMember(flatMemberAddRequest.getFlatId(),flatMember);
        }
        return flatMembersRepo.save(flatMember);
    }

    @Auditing(entity = "FlatMember",action = "EDIT")
    public FlatMember changeFlatMemberType(Long id , FlatMembershipType type){
        FlatMember flatMember = findFlatMemberById(id);
        flatMember.setType(type);
        return flatMembersRepo.save(flatMember);
    }

    @Auditing(entity = "FlatMember",action = "EDIT")
    public FlatMember removeFlatMember(Long id){
        FlatMember flatMember = findFlatMemberById(id);
        flatMember.setIsActive(false);
        return flatMembersRepo.save(flatMember);
    }
    @Auditing(entity = "FlatMember",action = "READ")

    @Override
    public List<FlatMember> getFlatMembers(FlatMembersFilter filter) {
        return flatMembersQueryBuilder.search(filter);
    }
}
