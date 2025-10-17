package com.project.societyManagement.entity;

import com.project.societyManagement.entity.common.AuditableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tenant_role_menu")
public class TenantRoleMenu extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "tenant_role_id")
    private TenantRoles tenantRoles;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;
}
