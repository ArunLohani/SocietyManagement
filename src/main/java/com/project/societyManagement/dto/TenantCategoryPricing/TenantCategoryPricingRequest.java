package com.project.societyManagement.dto.TenantCategoryPricing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantCategoryPricingRequest {
    private String category;
    private Double amount;
}
