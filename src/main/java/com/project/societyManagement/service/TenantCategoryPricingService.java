package com.project.societyManagement.service;

import com.project.societyManagement.dto.TenantCategoryPricing.TenantCategoryPricingResponse;
import com.project.societyManagement.entity.TenantCategoryPricing;
import com.project.societyManagement.entity.types.FlatCategory;
import com.project.societyManagement.queryBuilder.tenantCategoryPricing.TenantCategoryPricingFilter;

import java.util.List;

public interface TenantCategoryPricingService {
    public TenantCategoryPricing updatePricing(String category , Double amount);
    public List<TenantCategoryPricingResponse> getTenantCategoryPricing();
    public List<TenantCategoryPricing> searchTenantCategoryPricing(TenantCategoryPricingFilter tenantCategoryPricingFilter);
    public TenantCategoryPricing updatePenaltyFee(String category , Double amount);
}
