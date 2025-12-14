package com.example.ticketing.service.admin;

import com.example.ticketing.dto.admin.AdminAssignTicketRequest;
import com.example.ticketing.dto.admin.AdminCreateTicketRequest;
import com.example.ticketing.dto.admin.AdminInternalChatMessageDto;
import com.example.ticketing.dto.admin.AdminTicketActivityLogDto;
import com.example.ticketing.dto.admin.AdminTicketDto;
import com.example.ticketing.dto.admin.AdminUpdateTicketRequest;
import com.example.ticketing.dto.admin.AdminUpdateTicketStatusRequest;
import com.example.ticketing.entity.TicketPriority;
import com.example.ticketing.entity.TicketStatus;

import java.time.Instant;
import java.util.List;

public interface AdminTicketService {

    List<AdminTicketDto> getAllTickets(TicketStatus status,
                                       TicketPriority priority,
                                       Long createdById,
                                       Long assignedToId,
                                       Instant createdFrom,
                                       Instant createdTo);

    AdminTicketDto getTicketById(Long id);

    AdminTicketDto createTicket(AdminCreateTicketRequest request);

    AdminTicketDto updateTicket(Long id, AdminUpdateTicketRequest request);

    AdminTicketDto assignTicket(Long ticketId, AdminAssignTicketRequest request);

    AdminTicketDto updateTicketStatus(Long ticketId, AdminUpdateTicketStatusRequest request);

    List<AdminInternalChatMessageDto> getInternalChatMessages(Long ticketId);

    List<AdminTicketActivityLogDto> getActivityLogs(Long ticketId);
}
