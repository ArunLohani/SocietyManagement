package com.project.societyManagement.repository;

import com.project.societyManagement.entity.TenantRoleMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRoleMenuRepo extends JpaRepository<TenantRoleMenu,Long> {
}
