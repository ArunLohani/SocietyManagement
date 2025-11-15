package com.project.societyManagement.repository;

import com.project.societyManagement.entity.Complaints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintsRepo extends JpaRepository<Complaints,Long> {
}
