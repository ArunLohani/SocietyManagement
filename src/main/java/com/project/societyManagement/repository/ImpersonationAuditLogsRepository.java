package com.project.societyManagement.repository;

import com.project.societyManagement.entity.ImpersonationAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImpersonationAuditLogsRepository extends JpaRepository<ImpersonationAuditLog,Long> {
}
