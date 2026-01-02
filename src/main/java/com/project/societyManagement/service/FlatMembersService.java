package com.project.societyManagement.service;

import com.project.societyManagement.dto.FlatMember.FlatMemberAddRequest;
import com.project.societyManagement.entity.Flat;
import com.project.societyManagement.entity.FlatMember;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.FlatMembershipType;
import com.project.societyManagement.queryBuilder.flatMembers.FlatMembersFilter;

import java.util.List;

public interface FlatMembersService {
    public FlatMember flatMemberExists(Long flatId , Long userId);
    public FlatMember findFlatMemberById(Long id);
    public FlatMember addOwnerToFlat(Long flatId , Long userId);
    public FlatMember addMemberToFlat(FlatMemberAddRequest flatMemberAddRequest);
    public FlatMember changeFlatMemberType(Long id , FlatMembershipType type);
    public FlatMember removeFlatMember(Long id);
    public List<FlatMember> getFlatMembers(FlatMembersFilter filter);
}
