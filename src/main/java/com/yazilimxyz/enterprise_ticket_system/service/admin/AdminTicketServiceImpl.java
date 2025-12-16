package com.yazilimxyz.enterprise_ticket_system.service.admin;

import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminTicketResponseDto;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.TicketAssignRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.TicketFilterRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.TicketStatusUpdateRequest;
import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.exception.BadRequestException;
import com.yazilimxyz.enterprise_ticket_system.exception.NotFoundException;
import com.yazilimxyz.enterprise_ticket_system.mapper.AdminTicketMapper;
import com.yazilimxyz.enterprise_ticket_system.Repositories.TicketRepository;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import com.yazilimxyz.enterprise_ticket_system.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class AdminTicketServiceImpl implements AdminTicketService {

    private final TicketRepository ticketRepo;
    private final UserRepository userRepo;
    private final AdminTicketMapper mapper;

    @Override
    public Page<AdminTicketResponseDto> getTickets(TicketFilterRequest f) {
        // Şimdilik filtre uygulamıyoruz, sadece pagination:
        Page<Ticket> page = ticketRepo.findAll(
                PageRequest.of(f.getPage(), f.getSize(), Sort.by("id").descending()));
        return page.map(mapper::toDto);
    }

    @Override
    public AdminTicketResponseDto getTicket(Long id) {
        Ticket t = ticketRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found with id: " + id));
        return mapper.toDto(t);
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
        ticketRepo.save(t);
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
}
