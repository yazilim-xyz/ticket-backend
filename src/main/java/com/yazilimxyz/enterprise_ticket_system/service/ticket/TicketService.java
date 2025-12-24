package com.yazilimxyz.enterprise_ticket_system.service.ticket;

import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;
import com.yazilimxyz.enterprise_ticket_system.entities.TicketComment;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketPriority;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketCategory;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import com.yazilimxyz.enterprise_ticket_system.repository.TicketRepository;
import com.yazilimxyz.enterprise_ticket_system.repository.TicketCommentRepository;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketAssignRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketCommentCreateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketCreateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketStatusUpdateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.TicketStatisticsdto;
import com.yazilimxyz.enterprise_ticket_system.dto.TicketSimpledto;
import com.yazilimxyz.enterprise_ticket_system.dto.TicketDetaildto;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    
    // Get current user ID from security context
    private Long getCurrentUserId() {
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("Unauthorized");
        }
        return Long.valueOf(auth.getPrincipal().toString());
    }

   
    @Transactional(readOnly = true)
    public TicketStatisticsdto getMyTicketStatistics(OffsetDateTime startDate, OffsetDateTime endDate) {
        Long userId = getCurrentUserId();
        
        // Tarih aralığındaki ticketlar (İÇERİDE)
        long total = ticketRepository.countByAssignedToIdAndCreatedAtBetween(userId, startDate, endDate);
        long open = ticketRepository.countByAssignedToIdAndStatusAndDateRange(userId, TicketStatus.OPEN, startDate, endDate);
        long inProgress = ticketRepository.countByAssignedToIdAndStatusAndDateRange(userId, TicketStatus.IN_PROGRESS, startDate, endDate);
        long resolved = ticketRepository.countByAssignedToIdAndStatusAndDateRange(userId, TicketStatus.RESOLVED, startDate, endDate);
        long closed = ticketRepository.countByAssignedToIdAndStatusAndDateRange(userId, TicketStatus.CLOSED, startDate, endDate);

        
        // Tarih aralığından ÖNCE oluşturulanlar (OVERDUE - sadece geçmiş aylar, RESOLVED hariç)
        long overdue = ticketRepository.countOverdueTickets(userId, startDate);
        
        return TicketStatisticsdto.builder()
                .total(total)
                .opened(open)
                .inProgress(inProgress)
                .resolved(resolved)
                .closed(closed)
                .overdue(overdue)
                .build();
    }

    // 2. Get user's tickets with filters (simple list without details)
    @Transactional(readOnly = true)
    public List<TicketSimpledto> getMyTickets(TicketStatus status, OffsetDateTime startDate, OffsetDateTime endDate) {
        Long userId = getCurrentUserId();
        List<Ticket> tickets;
        
        // HER ÜÇÜ DE DOLU
        if (status != null && startDate != null && endDate != null) {
            tickets = ticketRepository.findByAssignedToIdAndStatusAndCreatedAtBetween(
                userId, status, startDate, endDate);
        } 
        // SADECE STATUS VAR
        else if (status != null && (startDate == null || endDate == null)) {
            tickets = ticketRepository.findByAssignedToIdAndStatus(userId, status);
        } 
        // SADECE TARİH ARALIĞI VAR
        else if (status == null && startDate != null && endDate != null) {
            tickets = ticketRepository.findMyTicketsFiltered(userId, null, startDate, endDate);
        } 
        // HİÇBİRİ YOK
        else {
            tickets = ticketRepository.findByAssignedToId(userId);
        }
        
        return tickets.stream()
                .map(this::convertToSimpleDto)
                .collect(Collectors.toList());
    }
    
    // 3. Get OVERDUE tickets (before start date)
    @Transactional(readOnly = true)
    public List<TicketSimpledto> getOverdueTickets(TicketStatus status, OffsetDateTime startDate) {
        Long userId = getCurrentUserId();
        List<Ticket> tickets = ticketRepository.findOverdueTickets(userId, status, startDate);
        
        return tickets.stream()
                .map(this::convertToSimpleDto)
                .collect(Collectors.toList());
    }
    
    private TicketSimpledto convertToSimpleDto(Ticket ticket) {
        return TicketSimpledto.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .category(ticket.getCategory())
                .createdByName(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getUsername() : null)
                .assignedToName(ticket.getAssignedTo() != null ? ticket.getAssignedTo().getUsername() : null)
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .dueDate(ticket.getDueDate())
                .build();
    }

    // 4. Get ticket detail with all information
    @Transactional(readOnly = true)
    public TicketDetaildto getTicketDetail(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));
        
        return TicketDetaildto.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .category(ticket.getCategory())
                .createdByName(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getUsername() : null)
                .createdById(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getId() : null)
                .assignedToName(ticket.getAssignedTo() != null ? ticket.getAssignedTo().getUsername() : null)
                .assignedToId(ticket.getAssignedTo() != null ? ticket.getAssignedTo().getId() : null)
                .dueDate(ticket.getDueDate())
                .resolutionSummary(ticket.getResolutionSummary())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .comments(ticket.getComments())
                .build();
    }

    // ==================== EXISTING METHODS ====================

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
}


