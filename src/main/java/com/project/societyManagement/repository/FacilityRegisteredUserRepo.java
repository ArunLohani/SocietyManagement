package com.project.societyManagement.repository;

import com.project.societyManagement.entity.FacilityRegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRegisteredUserRepo extends JpaRepository<FacilityRegisteredUser,Long> {
}
