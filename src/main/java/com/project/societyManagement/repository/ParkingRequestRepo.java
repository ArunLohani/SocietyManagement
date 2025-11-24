package com.project.societyManagement.repository;

import com.project.societyManagement.entity.ParkingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingRequestRepo extends JpaRepository<ParkingRequest,Long> {
}
