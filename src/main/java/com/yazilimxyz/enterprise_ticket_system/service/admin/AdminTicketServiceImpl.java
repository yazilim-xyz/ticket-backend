package com.yazilimxyz.enterprise_ticket_system.service.admin;

import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminTicketCreateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminTicketResponseDto;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminTicketUpdateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.TicketAssignRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.TicketFilterRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.TicketStatusUpdateRequest;
import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.entities.Role;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketCategory;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketPriority;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;
import com.yazilimxyz.enterprise_ticket_system.exception.BadRequestException;
import com.yazilimxyz.enterprise_ticket_system.exception.NotFoundException;
import com.yazilimxyz.enterprise_ticket_system.mapper.AdminTicketMapper;
import com.yazilimxyz.enterprise_ticket_system.Repositories.TicketRepository;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import com.yazilimxyz.enterprise_ticket_system.security.AuthenticatedUser;
import com.yazilimxyz.enterprise_ticket_system.service.notification.NotificationService;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTicketServiceImpl implements AdminTicketService {

    private final TicketRepository ticketRepo;
    private final UserRepository userRepo;
    private final AdminTicketMapper mapper;
    private final NotificationService notificationService;

    @Override
    public Page<AdminTicketResponseDto> getTickets(TicketFilterRequest f) {
        Pageable pageable = PageRequest.of(f.getPage(), f.getSize(), Sort.by("id").descending());
        Page<Ticket> page;

        if (f.getOwnerId() != null && f.getAssignedToId() != null) {
            page = ticketRepo.findByCreatedByIdOrAssignedToId(f.getOwnerId(), f.getAssignedToId(), pageable);
        } else if (f.getOwnerId() != null) {
            page = ticketRepo.findByCreatedById(f.getOwnerId(), pageable);
        } else if (f.getAssignedToId() != null) {
            page = ticketRepo.findByAssignedToId(f.getAssignedToId(), pageable);
        } else {
            page = ticketRepo.findAll(pageable);
        }

        return page.map(mapper::toDto);
    }

    @Override
    public AdminTicketResponseDto getTicket(Long id) {
        Ticket t = ticketRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found with id: " + id));
        return mapper.toDto(t);
    }

    @Override
    public AdminTicketResponseDto createTicket(AdminTicketCreateRequest r) {
        if (r.getTitle() == null || r.getTitle().isBlank()) {
            throw new BadRequestException("Title is required");
        }
        if (r.getDescription() == null || r.getDescription().isBlank()) {
            throw new BadRequestException("Description is required");
        }

        // JWT'den oluşturan kullanıcıyı al
        Long currentUserId = currentUserId();
        User createdBy = null;
        if (currentUserId != null) {
            createdBy = userRepo.findById(currentUserId)
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + currentUserId));
        }

        User assignedTo = null;
        if (r.getAssignedToUserId() != null) {
            assignedTo = userRepo.findById(r.getAssignedToUserId())
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + r.getAssignedToUserId()));
        }

        Ticket t = new Ticket();
        t.setTitle(r.getTitle());
        t.setDescription(r.getDescription());
        t.setPriority(r.getPriority() != null ? r.getPriority() : TicketPriority.MEDIUM);
        t.setCategory(r.getCategory() != null ? r.getCategory() : TicketCategory.OTHER);
        t.setStatus(TicketStatus.OPEN);
        t.setCreatedBy(createdBy);
        t.setAssignedTo(assignedTo);
        t.setDueDate(r.getDueDate());
        t.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        t.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        Ticket saved = ticketRepo.save(t);

        // Atanan kişiye bildirim gönder (oluşturan kişi değilse)
        Long actingUserId = currentUserId();
        if (assignedTo != null && !assignedTo.getId().equals(actingUserId)) {
            notificationService.createAndSendNotification(
                    assignedTo.getId(),
                    "Yeni Ticket Atandı",
                    String.format("Ticket #%d size atandı: %s", saved.getId(), saved.getTitle()),
                    NotificationType.TICKET_ASSIGNED,
                    saved.getId());
        }

        // Tüm adminlere bildirim gönder (işlemi yapan admin hariç)
        notifyAllAdmins(actingUserId, "Yeni Ticket Oluşturuldu",
                String.format("Ticket #%d oluşturuldu: %s", saved.getId(), saved.getTitle()),
                NotificationType.TICKET_ASSIGNED, saved.getId());

        return mapper.toDto(saved);
    }

    @Override
    public AdminTicketResponseDto updateTicket(Long id, AdminTicketUpdateRequest r) {
        Ticket t = ticketRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found with id: " + id));

        if (r.getTitle() == null || r.getTitle().isBlank()) {
            throw new BadRequestException("Title is required");
        }
        if (r.getDescription() == null || r.getDescription().isBlank()) {
            throw new BadRequestException("Description is required");
        }

        t.setTitle(r.getTitle());
        t.setDescription(r.getDescription());
        if (r.getPriority() != null) {
            t.setPriority(r.getPriority());
        }
        if (r.getCategory() != null) {
            t.setCategory(r.getCategory());
        }
        t.setDueDate(r.getDueDate());
        t.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        Ticket saved = ticketRepo.save(t);

        // Bildirim gönderirken güncelleyen kişiyi hariç tut
        Long actingUserId = currentUserId();

        // Atanan kişiye bildirim gönder (güncelleyen kişi değilse)
        if (saved.getAssignedTo() != null && !saved.getAssignedTo().getId().equals(actingUserId)) {
            notificationService.createAndSendNotification(
                    saved.getAssignedTo().getId(),
                    "Ticket Güncellendi",
                    String.format("Ticket #%d güncellendi: %s", saved.getId(), saved.getTitle()),
                    NotificationType.TICKET_STATUS_CHANGED,
                    saved.getId());
        }

        // Oluşturan kişiye de bildirim gönder (güncelleyen ve atanan kişi değilse)
        if (saved.getCreatedBy() != null &&
                !saved.getCreatedBy().getId().equals(actingUserId) &&
                (saved.getAssignedTo() == null
                        || !saved.getCreatedBy().getId().equals(saved.getAssignedTo().getId()))) {
            notificationService.createAndSendNotification(
                    saved.getCreatedBy().getId(),
                    "Ticket Güncellendi",
                    String.format("Ticket #%d güncellendi: %s", saved.getId(), saved.getTitle()),
                    NotificationType.TICKET_STATUS_CHANGED,
                    saved.getId());
        }

        // Tüm adminlere bildirim gönder (işlemi yapan admin hariç)
        notifyAllAdmins(actingUserId, "Ticket Güncellendi",
                String.format("Ticket #%d güncellendi: %s", saved.getId(), saved.getTitle()),
                NotificationType.TICKET_STATUS_CHANGED, saved.getId());

        return mapper.toDto(saved);
    }

    @Override
    public void updateTicketStatus(Long id, TicketStatusUpdateRequest r) {
        Ticket t = ticketRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found with id: " + id));

        if (r.getStatus() == null) {
            throw new BadRequestException("Status is required");
        }

        t.setStatus(r.getStatus());
        t.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        Ticket saved = ticketRepo.save(t);

        // Bildirim gönderirken güncelleyen kişiyi hariç tut
        Long actingUserId = currentUserId();

        // Atanan kişiye bildirim gönder (güncelleyen kişi değilse)
        if (saved.getAssignedTo() != null && !saved.getAssignedTo().getId().equals(actingUserId)) {
            notificationService.createAndSendNotification(
                    saved.getAssignedTo().getId(),
                    "Ticket Durumu Değişti",
                    String.format("Ticket #%d durumu '%s' olarak değiştirildi", saved.getId(), r.getStatus()),
                    NotificationType.TICKET_STATUS_CHANGED,
                    saved.getId());
        }

        // Oluşturan kişiye de bildirim gönder (güncelleyen ve atanan kişi değilse)
        if (saved.getCreatedBy() != null &&
                !saved.getCreatedBy().getId().equals(actingUserId) &&
                (saved.getAssignedTo() == null
                        || !saved.getCreatedBy().getId().equals(saved.getAssignedTo().getId()))) {
            notificationService.createAndSendNotification(
                    saved.getCreatedBy().getId(),
                    "Ticket Durumu Değişti",
                    String.format("Ticket #%d durumu '%s' olarak değiştirildi", saved.getId(), r.getStatus()),
                    NotificationType.TICKET_STATUS_CHANGED,
                    saved.getId());
        }

        // Tüm adminlere bildirim gönder (işlemi yapan admin hariç)
        notifyAllAdmins(actingUserId, "Ticket Durumu Değişti",
                String.format("Ticket #%d durumu '%s' olarak değiştirildi", saved.getId(), r.getStatus()),
                NotificationType.TICKET_STATUS_CHANGED, saved.getId());
    }

    @Override
    public void assignTicket(Long id, TicketAssignRequest r) {
        Ticket t = ticketRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found with id: " + id));

        if (r.getAssignedToUserId() == null) {
            throw new BadRequestException("assignedToUserId is required");
        }

        User u = userRepo.findById(r.getAssignedToUserId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + r.getAssignedToUserId()));

        t.setAssignedTo(u);
        Long currentUserId = currentUserId();
        if (currentUserId != null) {
            User admin = userRepo.findById(currentUserId)
                    .orElseThrow(() -> new NotFoundException("Acting admin user not found"));
            t.setCreatedBy(admin);
        }

        t.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        Ticket saved = ticketRepo.save(t);

        // Yeni atanan kişiye bildirim gönder (atayan kişi değilse)
        if (!u.getId().equals(currentUserId)) {
            notificationService.createAndSendNotification(
                    u.getId(),
                    "Yeni Ticket Atandı",
                    String.format("Ticket #%d size atandı: %s", saved.getId(), saved.getTitle()),
                    NotificationType.TICKET_ASSIGNED,
                    saved.getId());
        }

        // Tüm adminlere bildirim gönder (işlemi yapan admin hariç)
        notifyAllAdmins(currentUserId, "Ticket Atandı",
                String.format("Ticket #%d atandı: %s", saved.getId(), saved.getTitle()),
                NotificationType.TICKET_ASSIGNED, saved.getId());
    }

    @Override
    public void deleteTicket(Long id) {
        Ticket t = ticketRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found with id: " + id));

        t.setIsDeleted(true);
        t.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        ticketRepo.save(t);
    }

    @Override
    public void restoreTicket(Long id) {
        Ticket t = ticketRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found with id: " + id));

        t.setIsDeleted(false);
        t.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        ticketRepo.save(t);
    }

    private Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedUser user) {
            return user.id();
        }

        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Tüm adminlere bildirim gönder (işlemi yapan admin hariç)
     */
    private void notifyAllAdmins(Long excludeUserId, String title, String message,
            NotificationType type, Long relatedEntityId) {
        List<User> admins = userRepo.findByRole(Role.ADMIN);
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
