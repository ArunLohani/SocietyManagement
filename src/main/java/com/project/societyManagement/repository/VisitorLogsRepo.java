package com.project.societyManagement.repository;

import com.project.societyManagement.entity.VisitorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitorLogsRepo extends JpaRepository<VisitorLog,Long> {
}
