package com.project.societyManagement.controller;

import com.project.societyManagement.annotations.RequiresPermission;
import com.project.societyManagement.dto.Api.ApiResponse;
import com.project.societyManagement.dto.TenantCategoryPricing.TenantCategoryPricingRequest;
import com.project.societyManagement.dto.TenantCategoryPricing.TenantCategoryPricingResponse;
import com.project.societyManagement.entity.TenantCategoryPricing;
import com.project.societyManagement.service.TenantCategoryPricingService;
import com.project.societyManagement.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category-pricing")
public class TenantCategoryPricingController {

    private final TenantCategoryPricingService tenantCategoryPricingService;
    private final ValidationUtil validationUtil;

    @RequiresPermission(api="SEARCH_MAINTENANCE_PRICING")
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<TenantCategoryPricingResponse>>> getTenantCategoryPricing(){
        List<TenantCategoryPricingResponse> tenantCategoryPricings = tenantCategoryPricingService.getTenantCategoryPricing();
        ApiResponse<List<TenantCategoryPricingResponse>> response = new ApiResponse<>(true,
                "Flat Category Fees fetched successfully",tenantCategoryPricings);
        return ResponseEntity.ok(response);
    }

    @RequiresPermission(api="EDIT_MAINTENANCE_PRICING")
    @PostMapping("")
    public ResponseEntity<ApiResponse<TenantCategoryPricing>> updateCategoryPrice(@RequestBody TenantCategoryPricingRequest tenantCategoryPricingRequest){
        validationUtil.validate(tenantCategoryPricingRequest);
        TenantCategoryPricing tenantCategoryPricing = tenantCategoryPricingService.updatePricing(tenantCategoryPricingRequest.getCategory(),tenantCategoryPricingRequest.getAmount());
        ApiResponse<TenantCategoryPricing> response = new ApiResponse<>(true,
                "Flat Category Fees updated successfully",tenantCategoryPricing);
        return ResponseEntity.ok(response);
    }
    @RequiresPermission(api="EDIT_MAINTENANCE_PRICING")
    @PostMapping("/penalty")
    public ResponseEntity<ApiResponse<TenantCategoryPricing>> updatePenaltyFee(@RequestBody TenantCategoryPricingResponse tenantCategoryPricingRequest){
        validationUtil.validate(tenantCategoryPricingRequest);
        TenantCategoryPricing tenantCategoryPricing = tenantCategoryPricingService.updatePenaltyFee(tenantCategoryPricingRequest.getCategory(),tenantCategoryPricingRequest.getAmount());
        ApiResponse<TenantCategoryPricing> response = new ApiResponse<>(true,
                "Flat Category Penalty Fees updated successfully",tenantCategoryPricing);
        return ResponseEntity.ok(response);
    }

}
