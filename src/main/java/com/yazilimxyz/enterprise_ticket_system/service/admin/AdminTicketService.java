package com.yazilimxyz.enterprise_ticket_system.service.admin;

import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminTicketResponseDto;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.TicketAssignRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.TicketFilterRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.TicketStatusUpdateRequest;

import org.springframework.data.domain.Page;

public interface AdminTicketService {

    Page<AdminTicketResponseDto> getTickets(TicketFilterRequest filter);

    AdminTicketResponseDto getTicket(Long id);

    void updateTicketStatus(Long id, TicketStatusUpdateRequest request);

    void assignTicket(Long id, TicketAssignRequest request);
}
