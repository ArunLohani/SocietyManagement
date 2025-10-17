package com.project.societyManagement.repository;

import com.project.societyManagement.entity.TenantRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRoleRepo extends JpaRepository<TenantRoles,Long> {

}
