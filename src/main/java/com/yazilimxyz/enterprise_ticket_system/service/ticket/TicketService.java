package com.yazilimxyz.enterprise_ticket_system.service.ticket;

import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;
import com.yazilimxyz.enterprise_ticket_system.entities.TicketComment;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketPriority;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketCategory;
import com.yazilimxyz.enterprise_ticket_system.repository.TicketRepository;
import com.yazilimxyz.enterprise_ticket_system.dto.TicketDetaildto;
import com.yazilimxyz.enterprise_ticket_system.dto.TicketStatisticsdto;
import com.yazilimxyz.enterprise_ticket_system.dto.TicketStatusSummaryDto;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketAssignRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketCommentCreateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketCreateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketStatusUpdateRequest;
import com.yazilimxyz.enterprise_ticket_system.repository.TicketCommentRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service

public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketCommentRepository ticketCommentRepository;
    public TicketService(TicketRepository ticketRepository,
            UserRepository userRepository,
            TicketCommentRepository ticketCommentRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.ticketCommentRepository = ticketCommentRepository;
    }
    
    private Long getCurrentUserId() {
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("Unauthorized");
        }
        return Long.valueOf(auth.getPrincipal().toString());
    }

    public TicketStatusSummaryDto getMyTicketStatusSummary() {
        Long userId = getCurrentUserId();

        long total = ticketRepository.countByAssignedToId(userId);

        return TicketStatusSummaryDto.builder()
                .total(total)
                .open(ticketRepository.countByAssignedToIdAndStatus(userId, TicketStatus.OPEN))
                .waiting(ticketRepository.countByAssignedToIdAndStatus(userId, TicketStatus.WAITING))
                .inProgress(ticketRepository.countByAssignedToIdAndStatus(userId, TicketStatus.IN_PROGRESS))
                .resolved(ticketRepository.countByAssignedToIdAndStatus(userId, TicketStatus.RESOLVED))
                .closed(ticketRepository.countByAssignedToIdAndStatus(userId, TicketStatus.CLOSED))
                .build();
    }

    @Transactional
    public Ticket createTicket(TicketCreateRequest request) {
        User createdBy = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getCreatedById()));

        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority() != null ? request.getPriority() : TicketPriority.MEDIUM);
        ticket.setCategory(request.getCategory() != null ? request.getCategory() : TicketCategory.OTHER);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedBy(createdBy);

        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket assignTicket(Long ticketId, TicketAssignRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

        User assignedTo = userRepository.findById(request.getAssignedToId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getAssignedToId()));

        ticket.setAssignedTo(assignedTo);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket updateStatus(Long ticketId, TicketStatusUpdateRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

        ticket.setStatus(request.getStatus());
        return ticketRepository.save(ticket);
    }

    @Transactional
    public TicketComment addComment(Long ticketId, TicketCommentCreateRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getAuthorId()));

        TicketComment comment = new TicketComment();
        comment.setTicket(ticket);
        comment.setUser(author);
        comment.setCommentText(request.getContent());
        return ticketCommentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<TicketComment> getComments(Long ticketId) {
        return ticketCommentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);

    }
    
    @Transactional(readOnly = true)
    public List<Ticket> getMyTickets(TicketStatus status, OffsetDateTime startDate, OffsetDateTime endDate) {
        Long userId = getCurrentUserId();
        
        if (status != null && startDate != null && endDate != null) {
            // Status ve tarih aralığına göre filtrele
            return ticketRepository.findByAssignedToIdAndStatusAndCreatedAtBetween(
                userId, status, startDate, endDate);
        } else if (status != null) {
            // Sadece status'e göre filtrele
            return ticketRepository.findByAssignedToIdAndStatus(userId, status);
        } else if (startDate != null && endDate != null) {
            // Sadece tarih aralığına göre filtrele
            return ticketRepository.findByAssignedToIdAndCreatedAtBetween(userId, startDate, endDate);
        } else {
            // Filtre yok, tüm ticket'ları getir
            return ticketRepository.findByAssignedToId(userId);
        }
    }


              @Transactional(readOnly = true)
    public TicketDetaildto getTicketDetail(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));
        
        List<TicketComment> comments = ticketCommentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
        
        return TicketDetaildto.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .category(ticket.getCategory())
                .createdByName(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getFullName() : null)
                .createdById(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getId() : null)
                .assignedToName(ticket.getAssignedTo() != null ? ticket.getAssignedTo().getFullName() : null)
                .assignedToId(ticket.getAssignedTo() != null ? ticket.getAssignedTo().getId() : null)
                .dueDate(ticket.getDueDate())
                .resolutionSummary(ticket.getResolutionSummary())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .comments(comments)
                .build();
    }

    @Transactional(readOnly = true)
    public TicketStatisticsdto getMyTicketStatistics() {
        Long userId = getCurrentUserId();

        long total = ticketRepository.countByAssignedToId(userId);
        long opened = ticketRepository.countByAssignedToIdAndStatus(userId, TicketStatus.OPEN);
        long inProgress = ticketRepository.countByAssignedToIdAndStatus(userId, TicketStatus.IN_PROGRESS);
        long resolved  = ticketRepository.countByAssignedToIdAndStatus(userId, TicketStatus.RESOLVED);
        long closed = ticketRepository.countByAssignedToIdAndStatus(userId, TicketStatus.CLOSED);

        Ticket latestTicket = ticketRepository.findTopByAssignedToIdOrderByCreatedAtDesc(userId);

        return TicketStatisticsdto.builder()
                .total(total)
                .opened(opened)
                .inProgress(inProgress)
                .resolved(resolved)
                .closed(closed)
                .id(latestTicket != null ? latestTicket.getId() : null)
                .title(latestTicket != null ? latestTicket.getTitle() : null)
                .status(latestTicket != null ? latestTicket.getStatus() : null)
                .priority(latestTicket != null ? latestTicket.getPriority() : null)
                .createdAt(latestTicket != null ? latestTicket.getCreatedAt() : null)
                .dueDate(latestTicket != null ? latestTicket.getDueDate() : null)
                .build();
    }
}
    