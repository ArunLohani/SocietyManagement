package com.project.societyManagement.service.impl;

import com.project.societyManagement.config.TenantContextHolder;
import com.project.societyManagement.dto.TenantCategoryPricing.TenantCategoryPricingResponse;
import com.project.societyManagement.entity.Tenant;
import com.project.societyManagement.entity.TenantCategoryPricing;
import com.project.societyManagement.entity.types.FlatCategory;
import com.project.societyManagement.queryBuilder.tenantCategoryPricing.TenantCategoryPricingFilter;
import com.project.societyManagement.queryBuilder.tenantCategoryPricing.TenantCategoryPricingQueryBuilder;
import com.project.societyManagement.repository.TenantCategoryPricingRepo;
import com.project.societyManagement.service.TenantCategoryPricingService;
import com.project.societyManagement.service.TenantService;
import com.project.societyManagement.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenantCategoryPricingServiceImpl implements TenantCategoryPricingService {

    private final TenantCategoryPricingQueryBuilder tenantCategoryPricingQueryBuilder;
    private final TenantCategoryPricingRepo tenantCategoryPricingRepo;
    private final TenantService tenantService;
    private final ValidationUtil validationUtil;

    public TenantCategoryPricing updatePricing(String category , Double amount){
        ;
        TenantCategoryPricingFilter filter = new TenantCategoryPricingFilter();
        filter.setTenant(TenantContextHolder.getCurrentTenant());
        filter.setCategory(category);
        TenantCategoryPricing tenantCategoryPricing = null;
        try{
            tenantCategoryPricing = tenantCategoryPricingQueryBuilder.findById(filter);
            tenantCategoryPricing.setMonthlyFee(amount);
        } catch (Exception e) {
            Tenant tenant = tenantService.findTenantById(TenantContextHolder.getCurrentTenant());
            tenantCategoryPricing = TenantCategoryPricing.builder()
                    .tenant(tenant)
                    .category(FlatCategory.valueOf(category))
                    .monthlyFee(amount)
                    .isActive(true)
                    .build();
        }
        return tenantCategoryPricingRepo.save(tenantCategoryPricing);
    }

    public List<TenantCategoryPricingResponse> getTenantCategoryPricing(){
        TenantCategoryPricingFilter filter = new TenantCategoryPricingFilter();
        filter.setTenant(TenantContextHolder.getCurrentTenant());
        List<TenantCategoryPricing> tenantCategoryPricing = tenantCategoryPricingQueryBuilder.search(filter);
        Map<FlatCategory, TenantCategoryPricing> map = tenantCategoryPricing.stream()
                .collect(Collectors.toMap(TenantCategoryPricing::getCategory, p -> p));
        return Arrays.stream(FlatCategory.values())
                .map(category -> {
                    TenantCategoryPricing p = map.get(category);
                    return new TenantCategoryPricingResponse(
                            category.name(),
                            p != null ? p.getMonthlyFee() : null,
                            p !=null ? p.getPenaltyFee() : 0.0
                    );
                })
                .collect(Collectors.toList());
    }

    public List<TenantCategoryPricing> searchTenantCategoryPricing(TenantCategoryPricingFilter tenantCategoryPricingFilter){
        return tenantCategoryPricingQueryBuilder.search(tenantCategoryPricingFilter);
    }

    @Override
    public TenantCategoryPricing updatePenaltyFee(String category, Double amount) {
        TenantCategoryPricingFilter filter = new TenantCategoryPricingFilter();
        filter.setTenant(TenantContextHolder.getCurrentTenant());
        filter.setCategory(category);
        TenantCategoryPricing tenantCategoryPricing = null;
        try{
            tenantCategoryPricing = tenantCategoryPricingQueryBuilder.findById(filter);
            tenantCategoryPricing.setPenaltyFee(amount);
        } catch (Exception e) {
            Tenant tenant = tenantService.findTenantById(TenantContextHolder.getCurrentTenant());
            tenantCategoryPricing = TenantCategoryPricing.builder()
                    .tenant(tenant)
                    .category(FlatCategory.valueOf(category))
                    .penaltyFee(amount)
                    .isActive(true)
                    .build();
        }
        return tenantCategoryPricingRepo.save(tenantCategoryPricing);
    }

}
