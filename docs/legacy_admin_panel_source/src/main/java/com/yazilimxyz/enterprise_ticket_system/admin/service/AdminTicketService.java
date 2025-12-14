package com.yazilimxyz.enterprise_ticket_system.admin.service;

import com.yazilimxyz.enterprise_ticket_system.admin.dto.ticket.*;
import org.springframework.data.domain.Page;

public interface AdminTicketService {

    Page<AdminTicketResponseDto> getTickets(TicketFilterRequest filter);

    AdminTicketResponseDto getTicket(Long id);

    void updateTicketStatus(Long id, TicketStatusUpdateRequest request);

    void assignTicket(Long id, TicketAssignRequest request);
}
