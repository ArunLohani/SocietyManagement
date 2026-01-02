package com.project.societyManagement.repository;

import com.project.societyManagement.entity.VisitorRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitorRequestRepo extends JpaRepository<VisitorRequest,Long> {
}
