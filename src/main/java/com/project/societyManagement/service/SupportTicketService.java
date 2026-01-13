package com.project.societyManagement.service;

import com.project.societyManagement.dto.SupportTicket.TicketRaiseRequest;
import com.project.societyManagement.entity.SupportTicket;
import com.project.societyManagement.queryBuilder.supportTicket.SupportTicketFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SupportTicketService {

    public SupportTicket getTicketById(Long ticketId);

    public SupportTicket raiseSupportTicket(TicketRaiseRequest request);

    public SupportTicket changeSupportTicketStatus(Long ticketId , String status);

    public List<SupportTicket> getSupportTicket(SupportTicketFilter filter);

    public Page<SupportTicket> getSupportTicketPaginated(SupportTicketFilter filter, Pageable pageable) ;

}
