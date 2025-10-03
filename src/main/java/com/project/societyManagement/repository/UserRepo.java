package com.project.societyManagement.repository;

import com.project.societyManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {
   User findByEmail(String email);
}
