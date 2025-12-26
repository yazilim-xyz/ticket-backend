package com.yazilimxyz.enterprise_ticket_system.service.ticket;

import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;
import com.yazilimxyz.enterprise_ticket_system.entities.TicketComment;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.entities.Role;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketPriority;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.NotificationType;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketCategory;
import com.yazilimxyz.enterprise_ticket_system.Repositories.TicketRepository;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketAssignRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketCommentCreateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketCommentDto;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketCreateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketDto;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketResolutionStatsDTO;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketStatusUpdateRequest;
import com.yazilimxyz.enterprise_ticket_system.Repositories.TicketCommentRepository;
import com.yazilimxyz.enterprise_ticket_system.service.notification.NotificationService;
import com.yazilimxyz.enterprise_ticket_system.dto.TicketStatisticsdto;
import com.yazilimxyz.enterprise_ticket_system.dto.TicketSimpledto;
import com.yazilimxyz.enterprise_ticket_system.dto.TicketDetaildto;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        public TicketDto createTicket(TicketCreateRequest request) {
                // JWT'den kullanıcıyı al
                Long currentUserId = getCurrentUserId();
                User createdBy = userRepository.findById(currentUserId)
                                .orElseThrow(() -> new RuntimeException("User not found: " + currentUserId));

                Ticket ticket = new Ticket();
                ticket.setTitle(request.getTitle());
                ticket.setDescription(request.getDescription());
                ticket.setPriority(request.getPriority() != null ? request.getPriority() : TicketPriority.MEDIUM);
                ticket.setCategory(request.getCategory() != null ? request.getCategory() : TicketCategory.OTHER);
                ticket.setStatus(TicketStatus.OPEN);
                ticket.setCreatedBy(createdBy);

                // Atanacak kullanıcıyı kontrol et ve ata
                User assignedTo = userRepository.findById(request.getAssignedToId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Assigned user not found: " + request.getAssignedToId()));
                ticket.setAssignedTo(assignedTo);

                Ticket saved = ticketRepository.save(ticket);

                // Bildirim gönder
                notificationService.createAndSendNotification(
                                assignedTo.getId(),
                                "Yeni Ticket Atandı",
                                String.format("Ticket #%d size atandı: %s", ticket.getId(), ticket.getTitle()),
                                NotificationType.TICKET_ASSIGNED,
                                ticket.getId());

                return convertToDto(saved);
        }

        @Transactional
        public TicketDto assignTicket(Long ticketId, TicketAssignRequest request) {
                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

                User assignedTo = userRepository.findById(request.getAssignedToId())
                                .orElseThrow(() -> new RuntimeException(
                                                "User not found: " + request.getAssignedToId()));

                ticket.setAssignedTo(assignedTo);
                Ticket saved = ticketRepository.save(ticket);

                // Atanan kişiye bildirim gönder (atayan kişi değilse)
                Long currentUserId = getCurrentUserId();
                if (!assignedTo.getId().equals(currentUserId)) {
                        notificationService.createAndSendNotification(
                                        assignedTo.getId(),
                                        "Yeni Ticket Atandı",
                                        String.format("Ticket #%d size atandı: %s", ticket.getId(), ticket.getTitle()),
                                        NotificationType.TICKET_ASSIGNED,
                                        ticket.getId());
                }

                // Tüm adminlere bildirim gönder (işlemi yapan kişi hariç)
                notifyAllAdmins(currentUserId, "Ticket Atandı",
                                String.format("Ticket #%d atandı: %s", saved.getId(), saved.getTitle()),
                                NotificationType.TICKET_ASSIGNED, saved.getId());

                return convertToDto(saved);
        }

        @Transactional
        public TicketDto updateStatus(Long ticketId, TicketStatusUpdateRequest request) {
                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

                TicketStatus oldStatus = ticket.getStatus();
                ticket.setStatus(request.getStatus());
                Ticket saved = ticketRepository.save(ticket);

                Long currentUserId = getCurrentUserId();

                // Ticket sahibine bildirim gönder (değiştiren kişi değilse)
                if (ticket.getCreatedBy() != null && !oldStatus.equals(request.getStatus()) &&
                                !ticket.getCreatedBy().getId().equals(currentUserId)) {
                        notificationService.createAndSendNotification(
                                        ticket.getCreatedBy().getId(),
                                        "Ticket Durumu Değişti",
                                        String.format("Ticket #%d durumu '%s' olarak değiştirildi", ticket.getId(),
                                                        request.getStatus()),
                                        NotificationType.TICKET_STATUS_CHANGED,
                                        ticket.getId());
                }

                // Atanan kişiye de bildirim gönder (değiştiren kişi değilse ve farklı ise)
                if (ticket.getAssignedTo() != null &&
                                !ticket.getAssignedTo().getId().equals(currentUserId) &&
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

                // Tüm adminlere bildirim gönder (işlemi yapan kişi hariç)
                notifyAllAdmins(currentUserId, "Ticket Durumu Değişti",
                                String.format("Ticket #%d durumu '%s' olarak değiştirildi", saved.getId(),
                                                request.getStatus()),
                                NotificationType.TICKET_STATUS_CHANGED, saved.getId());

                return convertToDto(saved);
        }

        @Transactional
        public TicketCommentDto addComment(Long ticketId, TicketCommentCreateRequest request) {
                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

                // JWT'den kullanıcıyı al
                Long currentUserId = getCurrentUserId();
                User author = userRepository.findById(currentUserId)
                                .orElseThrow(() -> new RuntimeException("User not found: " + currentUserId));

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
                                        String.format("%s ticket #%d'e yorum yaptı",
                                                        author.getName() + " " + author.getSurname(), ticketId),
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
                                        String.format("%s ticket #%d'e yorum yaptı",
                                                        author.getName() + " " + author.getSurname(), ticketId),
                                        NotificationType.NEW_COMMENT,
                                        ticketId);
                }

                return convertToCommentDto(saved);
        }

        @Transactional(readOnly = true)
        public List<TicketCommentDto> getComments(Long ticketId) {
                // Optimized: fetches comments with users in single query (no N+1 problem)
                List<TicketComment> comments = ticketCommentRepository.findByTicketIdWithUser(ticketId);
                return comments.stream()
                                .map(this::convertToCommentDto)
                                .collect(Collectors.toList());
        }

        /**
         * Top 5 en çok ticket çözen çalışanları getir
         * Her biri için: çözülen sayı, çözülmeyen sayı, başarı yüzdesi, ortalama çözme
         * süresi
         */
        @Transactional(readOnly = true)
        public List<TicketResolutionStatsDTO> getTopTicketResolvers() {
                List<Ticket> allTickets = ticketRepository.findAll();

                // Atanan kullanıcılara göre grupla
                Map<User, List<Ticket>> ticketsByAssignee = allTickets.stream()
                                .filter(t -> t.getAssignedTo() != null && !t.getIsDeleted())
                                .collect(Collectors.groupingBy(Ticket::getAssignedTo));

                // İstatistikleri hesapla
                List<TicketResolutionStatsDTO> stats = new ArrayList<>();

                for (Map.Entry<User, List<Ticket>> entry : ticketsByAssignee.entrySet()) {
                        User assignee = entry.getKey();
                        List<Ticket> tickets = entry.getValue();

                        // Çözülen ticket'lar (RESOLVED, CLOSED)
                        List<Ticket> resolvedTickets = tickets.stream()
                                        .filter(t -> t.getStatus() == TicketStatus.RESOLVED
                                                        || t.getStatus() == TicketStatus.CLOSED)
                                        .collect(Collectors.toList());

                        // Çözülmeyen ticket'lar
                        Long unResolvedCount = (long) tickets.size() - resolvedTickets.size();
                        Long resolvedCount = (long) resolvedTickets.size();

                        // Başarı yüzdesi
                        Double successRate = (resolvedCount == 0) ? 0.0
                                        : (resolvedCount.doubleValue() / tickets.size()) * 100;

                        // Ortalama çözme süresi (dakika)
                        Long averageResolutionTime = 0L;
                        if (!resolvedTickets.isEmpty()) {
                                long totalMinutes = resolvedTickets.stream()
                                                .mapToLong(t -> {
                                                        OffsetDateTime created = t.getCreatedAt();
                                                        OffsetDateTime updated = t.getUpdatedAt();
                                                        if (created != null && updated != null) {
                                                                return java.time.temporal.ChronoUnit.MINUTES
                                                                                .between(created, updated);
                                                        }
                                                        return 0;
                                                })
                                                .sum();
                                averageResolutionTime = totalMinutes / resolvedTickets.size();
                        }

                        TicketResolutionStatsDTO dto = TicketResolutionStatsDTO.builder()
                                        .userId(assignee.getId())
                                        .userName(assignee.getName())
                                        .userSurname(assignee.getSurname())
                                        .userEmail(assignee.getEmail())
                                        .resolvedCount(resolvedCount)
                                        .unResolvedCount(unResolvedCount)
                                        .successRate(Math.round(successRate * 100.0) / 100.0) // 2 decimal
                                        .averageResolutionTime(averageResolutionTime)
                                        .build();

                        stats.add(dto);
                }

                // Çözülen ticket sayısına göre sırala ve ilk 5'i al
                return stats.stream()
                                .sorted((a, b) -> b.getResolvedCount().compareTo(a.getResolvedCount()))
                                .limit(5)
                                .collect(Collectors.toList());
        }

        // Get current user ID from security context
        private Long getCurrentUserId() {
                org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext()
                                .getAuthentication();
                if (auth == null || auth.getPrincipal() == null) {
                        throw new RuntimeException("Unauthorized");
                }
                return Long.valueOf(auth.getPrincipal().toString());
        }

        @Transactional(readOnly = true)
        public TicketStatisticsdto getMyTicketStatistics(OffsetDateTime startDate, OffsetDateTime endDate) {
                Long userId = getCurrentUserId();

                // Eğer tarihler null ise, tüm zamanları kapsayacak şekilde ayarla
                if (startDate == null) {
                        startDate = OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
                }
                if (endDate == null) {
                        endDate = OffsetDateTime.now(ZoneOffset.UTC).plusYears(10);
                }

                // Tarih aralığındaki ticketlar (İÇERİDE)
                long total = ticketRepository.countByAssignedToIdAndCreatedAtBetween(userId, startDate, endDate);
                long open = ticketRepository.countByAssignedToIdAndStatusAndDateRange(userId, TicketStatus.OPEN,
                                startDate, endDate);
                long inProgress = ticketRepository.countByAssignedToIdAndStatusAndDateRange(userId,
                                TicketStatus.IN_PROGRESS, startDate, endDate);
                long resolved = ticketRepository.countByAssignedToIdAndStatusAndDateRange(userId, TicketStatus.RESOLVED,
                                startDate, endDate);
                long closed = ticketRepository.countByAssignedToIdAndStatusAndDateRange(userId, TicketStatus.CLOSED,
                                startDate, endDate);

                // Tarih aralığından ÖNCE oluşturulanlar (OVERDUE - sadece geçmiş aylar,
                // RESOLVED hariç)
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
        public List<TicketSimpledto> getMyTickets(TicketStatus status, OffsetDateTime startDate,
                        OffsetDateTime endDate) {
                Long userId = getCurrentUserId();
                List<Ticket> tickets;

                // Tarihleri normalize et
                OffsetDateTime effectiveStartDate = startDate != null ? startDate
                                : OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
                OffsetDateTime effectiveEndDate = endDate != null ? endDate
                                : OffsetDateTime.now(ZoneOffset.UTC).plusYears(10);

                // SADECE STATUS VAR
                if (status != null) {
                        tickets = ticketRepository.findByAssignedToIdAndStatusAndCreatedAtBetween(
                                        userId, status, effectiveStartDate, effectiveEndDate);
                }
                // STATUS YOK, TÜM TİCKETLAR TARİH ARALIĞINDA
                else {
                        tickets = ticketRepository.findMyTicketsFiltered(userId, effectiveStartDate, effectiveEndDate);
                }

                return tickets.stream()
                                .map(this::convertToSimpleDto)
                                .collect(Collectors.toList());
        }

        // 3. Get OVERDUE tickets (before start date)
        @Transactional(readOnly = true)
        public List<TicketSimpledto> getOverdueTickets(TicketStatus status, OffsetDateTime startDate) {
                Long userId = getCurrentUserId();

                // Eğer startDate null ise, bugünün tarihini kullan
                OffsetDateTime effectiveStartDate = startDate != null ? startDate
                                : OffsetDateTime.now(ZoneOffset.UTC);

                List<Ticket> tickets = ticketRepository.findOverdueTickets(userId, effectiveStartDate);

                // Eğer status filtresi varsa, burada uygula
                if (status != null) {
                        tickets = tickets.stream()
                                        .filter(t -> t.getStatus() == status)
                                        .collect(Collectors.toList());
                }

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
                                .createdByName(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getName()
                                                : null)
                                .assignedToName(ticket.getAssignedTo() != null ? ticket.getAssignedTo().getName()
                                                : null)
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

                // Optimized: fetch comments with users in single query
                List<TicketCommentDto> commentDtos = ticketCommentRepository.findByTicketIdWithUser(ticketId).stream()
                                .map(this::convertToCommentDto)
                                .collect(Collectors.toList());

                return TicketDetaildto.builder()
                                .id(ticket.getId())
                                .title(ticket.getTitle())
                                .description(ticket.getDescription())
                                .status(ticket.getStatus())
                                .priority(ticket.getPriority())
                                .category(ticket.getCategory())
                                .createdByName(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getName()
                                                : null)
                                .createdById(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getId() : null)
                                .assignedToName(ticket.getAssignedTo() != null ? ticket.getAssignedTo().getName()
                                                : null)
                                .assignedToId(ticket.getAssignedTo() != null ? ticket.getAssignedTo().getId() : null)
                                .dueDate(ticket.getDueDate())
                                .resolutionSummary(ticket.getResolutionSummary())
                                .createdAt(ticket.getCreatedAt())
                                .updatedAt(ticket.getUpdatedAt())
                                .comments(commentDtos)
                                .build();
        }

        // Helper methods for entity to DTO conversion
        private TicketDto convertToDto(Ticket ticket) {
                return TicketDto.builder()
                                .id(ticket.getId())
                                .title(ticket.getTitle())
                                .description(ticket.getDescription())
                                .status(ticket.getStatus())
                                .priority(ticket.getPriority())
                                .category(ticket.getCategory())
                                .createdById(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getId() : null)
                                .createdByName(ticket.getCreatedBy() != null
                                                ? ticket.getCreatedBy().getName() + " "
                                                                + ticket.getCreatedBy().getSurname()
                                                : null)
                                .assignedToId(ticket.getAssignedTo() != null ? ticket.getAssignedTo().getId() : null)
                                .assignedToName(ticket.getAssignedTo() != null
                                                ? ticket.getAssignedTo().getName() + " "
                                                                + ticket.getAssignedTo().getSurname()
                                                : null)
                                .dueDate(ticket.getDueDate())
                                .resolutionSummary(ticket.getResolutionSummary())
                                .createdAt(ticket.getCreatedAt())
                                .updatedAt(ticket.getUpdatedAt())
                                .build();
        }

        private TicketCommentDto convertToCommentDto(TicketComment comment) {
                return TicketCommentDto.builder()
                                .id(comment.getId())
                                .ticketId(comment.getTicket() != null ? comment.getTicket().getId() : null)
                                .userId(comment.getUser() != null ? comment.getUser().getId() : null)
                                .userName(comment.getUser() != null
                                                ? comment.getUser().getName() + " " + comment.getUser().getSurname()
                                                : null)
                                .commentText(comment.getCommentText())
                                .createdAt(comment.getCreatedAt())
                                .build();
        }

        @Transactional
        public TicketDto updateResolution(Long ticketId, String resolutionSummary) {
                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));

                ticket.setResolutionSummary(resolutionSummary);
                ticket.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

                Ticket saved = ticketRepository.save(ticket);

                Long currentUserId = getCurrentUserId();

                // Ticket sahibine bildirim gönder (işlemi yapan kişi değilse)
                if (ticket.getCreatedBy() != null && !ticket.getCreatedBy().getId().equals(currentUserId)) {
                        notificationService.createAndSendNotification(
                                        ticket.getCreatedBy().getId(),
                                        "Ticket Çözüm Eklendi",
                                        String.format("Ticket #%d için çözüm özeti eklendi", ticket.getId()),
                                        NotificationType.TICKET_STATUS_CHANGED,
                                        ticket.getId());
                }

                // Tüm adminlere bildirim gönder (işlemi yapan kişi hariç)
                notifyAllAdmins(currentUserId, "Ticket Çözüm Eklendi",
                                String.format("Ticket #%d için çözüm özeti eklendi", saved.getId()),
                                NotificationType.TICKET_STATUS_CHANGED, saved.getId());

                return convertToDto(saved);
        }

        /**
         * Tüm adminlere bildirim gönder (işlemi yapan kişi hariç)
         */
        private void notifyAllAdmins(Long excludeUserId, String title, String message,
                        NotificationType type, Long relatedEntityId) {
                List<User> admins = userRepository.findByRole(Role.ADMIN);
                for (User admin : admins) {
                        if (excludeUserId == null || !admin.getId().equals(excludeUserId)) {
                                notificationService.createAndSendNotification(
                                                admin.getId(),
                                                title,
                                                message,
                                                type,
                                                relatedEntityId);
                        }
                }
        }
}
