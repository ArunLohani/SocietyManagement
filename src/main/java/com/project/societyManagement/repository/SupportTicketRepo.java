package com.project.societyManagement.repository;

import com.project.societyManagement.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportTicketRepo extends JpaRepository<SupportTicket , Long> {
}
