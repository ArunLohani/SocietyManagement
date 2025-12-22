package com.project.societyManagement.queryBuilder.tenantCategoryPricing;

import com.project.societyManagement.entity.Tenant;
import com.project.societyManagement.entity.types.FlatCategory;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantCategoryPricingFilter {
    private Boolean isActive = true;
    private Long id;
    private Long tenant;
    private String category;
    private Double monthlyFee;
}
