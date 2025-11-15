package com.project.societyManagement.queryBuilder.apiPermissionMapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiPermissionMappingFilter {

    private Long id;
    private String api;
    private Boolean isActive = true;
}
