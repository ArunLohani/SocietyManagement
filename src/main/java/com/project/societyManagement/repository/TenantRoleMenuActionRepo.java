package com.project.societyManagement.repository;

import com.project.societyManagement.entity.TenantRoleMenuAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRoleMenuActionRepo extends JpaRepository<TenantRoleMenuAction,Long> {
}
