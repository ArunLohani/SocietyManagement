package com.project.societyManagement.repository;

import com.project.societyManagement.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepo extends JpaRepository<Notice,Long> {
}
