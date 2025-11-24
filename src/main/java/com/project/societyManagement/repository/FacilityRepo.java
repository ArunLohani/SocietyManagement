package com.project.societyManagement.repository;

import com.project.societyManagement.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepo extends JpaRepository<Facility,Long> {
}
