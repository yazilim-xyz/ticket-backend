package com.yazilimxyz.enterprise_ticket_system.service.ticket;

import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;
import com.yazilimxyz.enterprise_ticket_system.entities.TicketComment;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketPriority;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.NotificationType;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketCategory;
import com.yazilimxyz.enterprise_ticket_system.Repositories.TicketRepository;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketAssignRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketCommentCreateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketCreateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketStatusUpdateRequest;
import com.yazilimxyz.enterprise_ticket_system.Repositories.TicketCommentRepository;
import com.yazilimxyz.enterprise_ticket_system.dto.notification.NotificationDto;
import com.yazilimxyz.enterprise_ticket_system.service.notification.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketService {
        private final TicketRepository ticketRepository;
        private final UserRepository userRepository;
        private final TicketCommentRepository ticketCommentRepository;
        private final NotificationService notificationService;

        public TicketService(TicketRepository ticketRepository,
                        UserRepository userRepository,
                        TicketCommentRepository ticketCommentRepository,
                        NotificationService notificationService) {
                this.ticketRepository = ticketRepository;
                this.userRepository = userRepository;
                this.ticketCommentRepository = ticketCommentRepository;
                this.notificationService = notificationService;
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
                                .orElseThrow(() -> new RuntimeException(
                                                "User not found: " + request.getAssignedToId()));

                ticket.setAssignedTo(assignedTo);
                Ticket saved = ticketRepository.save(ticket);

                // Bildirim gönder
                notificationService.createAndSendNotification(
                                assignedTo.getId(),
                                "Yeni Ticket Atandı",
                                String.format("Ticket #%d size atandı: %s", ticket.getId(), ticket.getTitle()),
                                NotificationType.TICKET_ASSIGNED,
                                ticket.getId());

                return saved;
        }

        @Transactional
        public Ticket updateStatus(Long ticketId, TicketStatusUpdateRequest request) {
                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

                TicketStatus oldStatus = ticket.getStatus();
                ticket.setStatus(request.getStatus());
                Ticket saved = ticketRepository.save(ticket);

                // Ticket sahibine bildirim gönder
                if (ticket.getCreatedBy() != null && !oldStatus.equals(request.getStatus())) {
                        notificationService.createAndSendNotification(
                                        ticket.getCreatedBy().getId(),
                                        "Ticket Durumu Değişti",
                                        String.format("Ticket #%d durumu '%s' olarak değiştirildi", ticket.getId(),
                                                        request.getStatus()),
                                        NotificationType.TICKET_STATUS_CHANGED,
                                        ticket.getId());
                }

                // Atanan kişiye de bildirim gönder (eğer farklı ise)
                if (ticket.getAssignedTo() != null &&
                                ticket.getCreatedBy() != null &&
                                !ticket.getAssignedTo().getId().equals(ticket.getCreatedBy().getId())) {
                        notificationService.createAndSendNotification(
                                        ticket.getAssignedTo().getId(),
                                        "Ticket Durumu Değişti",
                                        String.format("Ticket #%d durumu '%s' olarak değiştirildi", ticket.getId(),
                                                        request.getStatus()),
                                        NotificationType.TICKET_STATUS_CHANGED,
                                        ticket.getId());
                }

                return saved;
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
                TicketComment saved = ticketCommentRepository.save(comment);

                // Ticket sahibine bildirim gönder (eğer yorum yapan kendisi değilse)
                if (ticket.getCreatedBy() != null && !ticket.getCreatedBy().getId().equals(author.getId())) {
                        notificationService.createAndSendNotification(
                                        ticket.getCreatedBy().getId(),
                                        "Yeni Yorum",
                                        String.format("%s ticket #%d'e yorum yaptı", author.getFullName(), ticketId),
                                        NotificationType.NEW_COMMENT,
                                        ticketId);
                }

                // Atanan kişiye de bildirim gönder (eğer farklı ise ve yorum yapan kendisi
                // değilse)
                if (ticket.getAssignedTo() != null &&
                                !ticket.getAssignedTo().getId().equals(author.getId()) &&
                                (ticket.getCreatedBy() == null
                                                || !ticket.getAssignedTo().getId()
                                                                .equals(ticket.getCreatedBy().getId()))) {
                        notificationService.createAndSendNotification(
                                        ticket.getAssignedTo().getId(),
                                        "Yeni Yorum",
                                        String.format("%s ticket #%d'e yorum yaptı", author.getFullName(), ticketId),
                                        NotificationType.NEW_COMMENT,
                                        ticketId);
                }

                return saved;
        }

        @Transactional(readOnly = true)
        public List<TicketComment> getComments(Long ticketId) {
                return ticketCommentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
        }
}
