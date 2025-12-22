package com.project.societyManagement.queryBuilder.flatMembers;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlatMembersFilter {
    private Long flat;
    private Long user;
    private String type;
    private Long id;
    private Boolean isActive = true;
}
