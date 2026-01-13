package com.project.societyManagement.service.impl;

import com.project.societyManagement.dto.SupportTicket.TicketRaiseRequest;
import com.project.societyManagement.entity.SupportTicket;
import com.project.societyManagement.entity.User;
import com.project.societyManagement.entity.types.TicketStatus;
import com.project.societyManagement.queryBuilder.supportTicket.SupportTicketFilter;
import com.project.societyManagement.queryBuilder.supportTicket.SupportTicketQueryBuilder;
import com.project.societyManagement.repository.SupportTicketRepo;
import com.project.societyManagement.service.SupportTicketService;
import com.project.societyManagement.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketQueryBuilder supportTicketQueryBuilder;
    private final SupportTicketRepo supportTicketRepo;
    private final ValidationUtil validationUtil;

    public SupportTicket getTicketById(Long ticketId){
        SupportTicketFilter filter = new SupportTicketFilter();
        filter.setId(ticketId);
        return supportTicketQueryBuilder.findById(filter);
    }

    public SupportTicket raiseSupportTicket(TicketRaiseRequest request){
        validationUtil.validate(request);
        SupportTicket ticket = new SupportTicket();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ticket.setRaisedBy(user);
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        return supportTicketRepo.save(ticket);
    }

    public SupportTicket changeSupportTicketStatus(Long ticketId , String status){
        SupportTicket ticket = getTicketById(ticketId);
        ticket.setStatus(TicketStatus.valueOf(status));
        return supportTicketRepo.save(ticket);
    }

    public List<SupportTicket> getSupportTicket(SupportTicketFilter filter){
        return supportTicketQueryBuilder.search(filter);
    }

    public Page<SupportTicket> getSupportTicketPaginated(SupportTicketFilter filter, Pageable pageable) {
            return supportTicketQueryBuilder.searchPaginated(filter,pageable);
    }

}
