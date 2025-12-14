package com.yazilimxyz.enterprise_ticket_system.admin.service;

import com.yazilimxyz.enterprise_ticket_system.admin.dto.ticket.*;
import com.yazilimxyz.enterprise_ticket_system.admin.exception.NotFoundException;
import com.yazilimxyz.enterprise_ticket_system.admin.mapper.AdminTicketMapper;
import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.repository.TicketRepository;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

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
                PageRequest.of(f.getPage(), f.getSize(), Sort.by("id").descending())
        );
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

        // Sende status String, enum yok:
        t.setStatus(r.getStatus().toUpperCase());
        ticketRepo.save(t);
    }

    @Override
    public void assignTicket(Long id, TicketAssignRequest r) {
        Ticket t = ticketRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found with id: " + id));

        User u = userRepo.findById(r.getAssignedToUserId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + r.getAssignedToUserId()));

        // Entity’de alan adın büyük ihtimalle assignedUser:
        t.setAssignedUser(u);
        // Eğer alan adın farklıysa (ör: setAssigned_user), bunu kendine göre düzelt.

        ticketRepo.save(t);
    }
}
