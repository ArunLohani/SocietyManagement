package com.project.societyManagement.dto.TenantCategoryPricing;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantCategoryPricingRequest {
    @NotEmpty(message = "Category cannot be empty.")
    private String category;
    @NotEmpty(message = "Amount cannot be empty.")
    private Double amount;
}
