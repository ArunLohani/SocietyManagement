package com.project.societyManagement.dto.TenantCategoryPricing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantCategoryPricingResponse {
    private String category;
    private Double amount;
    private Double penalty;
}
