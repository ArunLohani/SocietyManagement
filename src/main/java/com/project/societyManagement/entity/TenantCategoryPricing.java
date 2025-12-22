package com.project.societyManagement.entity;

import com.project.societyManagement.entity.common.AuditableEntity;
import com.project.societyManagement.entity.types.FlatCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "tenant_category_pricing")
public class TenantCategoryPricing extends AuditableEntity {
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @Enumerated(EnumType.STRING)
    private FlatCategory category;
    @Column(name = "monthly_fee")
    private Double monthlyFee;
    @Column(name = "penalty_fee")
    private Double penaltyFee;
}
