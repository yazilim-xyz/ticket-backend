package com.example.ticketing.service.impl;

import com.example.ticketing.dto.admin.AdminAssignTicketRequest;
import com.example.ticketing.dto.admin.AdminCreateTicketRequest;
import com.example.ticketing.dto.admin.AdminInternalChatMessageDto;
import com.example.ticketing.dto.admin.AdminTicketActivityLogDto;
import com.example.ticketing.dto.admin.AdminTicketDto;
import com.example.ticketing.dto.admin.AdminUpdateTicketRequest;
import com.example.ticketing.dto.admin.AdminUpdateTicketStatusRequest;
import com.example.ticketing.entity.InternalChat;
import com.example.ticketing.entity.Ticket;
import com.example.ticketing.entity.TicketActivityLog;
import com.example.ticketing.entity.TicketPriority;
import com.example.ticketing.entity.TicketStatus;
import com.example.ticketing.entity.User;
import com.example.ticketing.exception.ResourceNotFoundException;
import com.example.ticketing.mapper.AdminInternalChatMapper;
import com.example.ticketing.mapper.AdminTicketMapper;
import com.example.ticketing.mapper.AdminTicketActivityLogMapper;
import com.example.ticketing.repository.InternalChatRepository;
import com.example.ticketing.repository.TicketActivityLogRepository;
import com.example.ticketing.repository.TicketRepository;
import com.example.ticketing.repository.UserRepository;
import com.example.ticketing.service.admin.AdminTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminTicketServiceImpl implements AdminTicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketActivityLogRepository ticketActivityLogRepository;
    private final InternalChatRepository internalChatRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AdminTicketDto> getAllTickets(TicketStatus status,
                                              TicketPriority priority,
                                              Long createdById,
                                              Long assignedToId,
                                              Instant createdFrom,
                                              Instant createdTo) {
        Specification<Ticket> spec = TicketSpecifications.withFilters(status, priority, createdById, assignedToId, createdFrom, createdTo);
        return ticketRepository.findAll(spec)
                .stream()
                .map(AdminTicketMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminTicketDto getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));
        return AdminTicketMapper.toDto(ticket);
    }

    @Override
    public AdminTicketDto createTicket(AdminCreateTicketRequest request) {
        User creator = getUserOrThrow(request.getCreatedById());
        User assignee = request.getAssignedToId() != null ? getUserOrThrow(request.getAssignedToId()) : null;
        Ticket ticket = AdminTicketMapper.fromCreateRequest(request, creator, assignee);
        Ticket saved = ticketRepository.save(ticket);
        logActivity(saved, "TICKET_CREATED", null, saved.getStatus().name());
        if (assignee != null) {
            logActivity(saved, "ASSIGNED", null, assignee.getFullName());
        }
        return AdminTicketMapper.toDto(saved);
    }

    @Override
    public AdminTicketDto updateTicket(Long id, AdminUpdateTicketRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));

        TicketStatus oldStatus = ticket.getStatus();
        Long oldAssigneeId = ticket.getAssignedTo() != null ? ticket.getAssignedTo().getId() : null;
        String oldAssigneeName = ticket.getAssignedTo() != null ? ticket.getAssignedTo().getFullName() : "unassigned";

        User assignee = null;
        if (request.getAssignedToId() != null) {
            assignee = getUserOrThrow(request.getAssignedToId());
        }
        AdminTicketMapper.applyUpdate(ticket, request, assignee);
        Ticket saved = ticketRepository.save(ticket);

        if (request.getStatus() != null && request.getStatus() != oldStatus) {
            logActivity(saved, "STATUS_CHANGED", oldStatus != null ? oldStatus.name() : null, request.getStatus().name());
        }
        if (request.getAssignedToId() != null && (oldAssigneeId == null || !oldAssigneeId.equals(request.getAssignedToId()))) {
            logActivity(saved, "ASSIGNED_CHANGED", oldAssigneeName, assignee != null ? assignee.getFullName() : "unassigned");
        }

        return AdminTicketMapper.toDto(saved);
    }

    @Override
    public AdminTicketDto assignTicket(Long ticketId, AdminAssignTicketRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + ticketId));
        User assignee = getUserOrThrow(request.getUserId());
        Long oldAssigneeId = ticket.getAssignedTo() != null ? ticket.getAssignedTo().getId() : null;

        AdminTicketMapper.applyAssignment(ticket, assignee);
        Ticket saved = ticketRepository.save(ticket);
        if (oldAssigneeId == null || !oldAssigneeId.equals(assignee.getId())) {
            logActivity(saved, "ASSIGNED", null, assignee.getFullName());
        }
        return AdminTicketMapper.toDto(saved);
    }

    @Override
    public AdminTicketDto updateTicketStatus(Long ticketId, AdminUpdateTicketStatusRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + ticketId));
        TicketStatus oldStatus = ticket.getStatus();
        AdminTicketMapper.applyStatus(ticket, request.getStatus());
        Ticket saved = ticketRepository.save(ticket);
        if (request.getStatus() != null && request.getStatus() != oldStatus) {
            logActivity(saved, "STATUS_CHANGED", oldStatus != null ? oldStatus.name() : null, request.getStatus().name());
        }
        return AdminTicketMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminInternalChatMessageDto> getInternalChatMessages(Long ticketId) {
        ensureTicketExists(ticketId);
        List<InternalChat> chats = internalChatRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
        return chats.stream()
                .map(AdminInternalChatMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminTicketActivityLogDto> getActivityLogs(Long ticketId) {
        ensureTicketExists(ticketId);
        return ticketActivityLogRepository.findByTicketIdOrderByCreatedAtDesc(ticketId)
                .stream()
                .map(AdminTicketActivityLogMapper::toDto)
                .toList();
    }

    private void ensureTicketExists(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Ticket not found with id " + ticketId);
        }
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    private void logActivity(Ticket ticket, String action, String oldValue, String newValue) {
        TicketActivityLog log = TicketActivityLog.builder()
                .ticket(ticket)
                .performedBy(getCurrentUser().orElse(null))
                .action(action)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();
        ticketActivityLogRepository.save(log);
    }

    private Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }
        return userRepository.findByEmail(authentication.getName());
    }
}
