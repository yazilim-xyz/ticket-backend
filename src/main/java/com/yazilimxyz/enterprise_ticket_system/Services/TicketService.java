package com.example.ticketapp.ticket;

import com.example.ticketapp.user.User;
import com.example.ticketapp.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        TicketComment comment = new TicketComment(ticket, author, request.getContent());
        return ticketCommentRepository.save(comment);
    }
    @Transactional(readOnly = true)
    public List<TicketComment> getComments(Long ticketId) {
        return ticketCommentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
    }
}
