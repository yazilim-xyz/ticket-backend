package com.yazilimxyz.enterprise_ticket_system.service.admin;

import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminTicketResponseDto;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminTicketUpdateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.TicketAssignRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.TicketFilterRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.TicketStatusUpdateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminTicketCreateRequest;

import org.springframework.data.domain.Page;

public interface AdminTicketService {

    Page<AdminTicketResponseDto> getTickets(TicketFilterRequest filter);

    AdminTicketResponseDto getTicket(Long id);

    AdminTicketResponseDto createTicket(AdminTicketCreateRequest request);

    AdminTicketResponseDto updateTicket(Long id, AdminTicketUpdateRequest request);

    void updateTicketStatus(Long id, TicketStatusUpdateRequest request);

    void assignTicket(Long id, TicketAssignRequest request);

    void deleteTicket(Long id);

    void restoreTicket(Long id);
}
