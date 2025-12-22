package com.project.societyManagement.queryBuilder.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFilter {

    private Long userId;
    private String email;
    private String name;
    private Long tenantId;
    private Boolean isActive = true;
//    private String phoneNumber;

}
