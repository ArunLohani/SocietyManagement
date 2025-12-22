package com.project.societyManagement.controller;

import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.FlatMember.FlatMemberAddRequest;
import com.project.societyManagement.entity.FlatMember;
import com.project.societyManagement.entity.types.FlatMembershipType;
import com.project.societyManagement.service.FlatMembersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flat-member")
@RequiredArgsConstructor
public class FlatMemberController {

    private final FlatMembersService flatMembersService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FlatMember>> getFlatMemberById(@PathVariable Long id){
        FlatMember flatMember = flatMembersService.findFlatMemberById(id);
        ApiResponse<FlatMember> response = new ApiResponse<>(true,"FlatMember fetched successfully",flatMember);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/add-owner/{flatId}/{userId}")
    public ResponseEntity<ApiResponse<FlatMember>> addOwnerToFlat(@PathVariable(name = "flatId") Long flatId , @PathVariable(name = "userId") Long userId){
        FlatMember flatMember = flatMembersService.addOwnerToFlat(flatId,userId);
        ApiResponse<FlatMember> response = new ApiResponse<>(true,"Owner Type added to Flat Member",flatMember);
        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<FlatMember>> addMemberToFlat(@RequestBody FlatMemberAddRequest flatMemberAddRequest){
        FlatMember flatMember = flatMembersService.addMemberToFlat(flatMemberAddRequest);
        ApiResponse<FlatMember> response = new ApiResponse<>(true,"Member added to Flat Member",flatMember);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<FlatMember>> removeMemberFromFlat(@PathVariable  Long id){
        FlatMember flatMember = flatMembersService.removeFlatMember(id);
        ApiResponse<FlatMember> response = new ApiResponse<>(true,"Member removed from Flat",flatMember);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FlatMember>> updateMemberType(@PathVariable Long id,@RequestBody FlatMembershipType type){
        FlatMember flatMember = flatMembersService.changeFlatMemberType(id,type);
        ApiResponse<FlatMember> response = new ApiResponse<>(true,"Member type updated in Flat",flatMember);
        return ResponseEntity.ok(response);
    }

}
