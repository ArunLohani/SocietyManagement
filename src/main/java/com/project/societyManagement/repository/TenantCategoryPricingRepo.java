package com.project.societyManagement.repository;

import com.project.societyManagement.entity.TenantCategoryPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantCategoryPricingRepo extends JpaRepository<TenantCategoryPricing,Long> {



}
