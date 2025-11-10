package com.project.societyManagement.queryBuilder.tenant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantFilter {
    private Long id;
    private String name;
    private Boolean isActive = true;
}
