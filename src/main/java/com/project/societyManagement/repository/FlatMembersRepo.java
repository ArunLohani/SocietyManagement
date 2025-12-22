package com.project.societyManagement.repository;

import com.project.societyManagement.entity.FlatMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlatMembersRepo extends JpaRepository<FlatMember,Long> {
}
