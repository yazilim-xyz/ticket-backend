package com.yazilimxyz.enterprise_ticket_system.service.ticket;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yazilimxyz.enterprise_ticket_system.Repositories.TicketCommentRepository;
import com.yazilimxyz.enterprise_ticket_system.Repositories.TicketRepository;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketAssignRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketCommentCreateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketCreateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketDto;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketStatusUpdateRequest;
import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;
import com.yazilimxyz.enterprise_ticket_system.entities.TicketComment;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketCategory;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketPriority;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;

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

    @Transactional
    public Ticket createTicket(TicketCreateRequest request, Long createdById) {
        User createdBy = userRepository.findById(createdById)
                .orElseThrow(() -> new RuntimeException("User not found: " + createdById));

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

    public TicketDto getTicket(Long id) {
    Ticket t = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));

    Long createdById = null;
    String createdByName = null;

    if (t.getCreatedBy() != null) {
        createdById = t.getCreatedBy().getId();
        createdByName = t.getCreatedBy().getFullName();
    }

    Long assignedToId = null;
    String assignedToName = null;

    if (t.getAssignedTo() != null) {
        assignedToId = t.getAssignedTo().getId();
        assignedToName = t.getAssignedTo().getFullName();
    }

    return new TicketDto(
            t.getId(),
            t.getTitle(),
            t.getDescription(),
            createdById,
            createdByName,
            assignedToId,
            assignedToName
    );
}


    @Transactional(readOnly = true)
    public List<TicketComment> getComments(Long ticketId) {
        return ticketCommentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
    }
}
