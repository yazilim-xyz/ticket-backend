package com.yazilimxyz.enterprise_ticket_system.service.admin;

import com.yazilimxyz.enterprise_ticket_system.Repositories.TicketRepository;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminTicketResponseDto;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.UserStatsDto;
import com.yazilimxyz.enterprise_ticket_system.entities.Role;
import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminUserServiceIntegrationTests {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void getUserTickets_andStats_shouldIncludeCreatedAndAssignedTickets() {
        User user = new User();
        user.setFullName("Stats User");
        user.setEmail("stats-user+" + System.currentTimeMillis() + "@local");
        user.setPasswordHash("hash");
        user.setRole(Role.USER);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        Ticket createdTicket = new Ticket();
        createdTicket.setTitle("Created ticket");
        createdTicket.setDescription("desc");
        createdTicket.setStatus(TicketStatus.RESOLVED);
        createdTicket.setCreatedBy(savedUser);
        Ticket savedCreated = ticketRepository.save(createdTicket);

        Ticket assignedOpen = new Ticket();
        assignedOpen.setTitle("Assigned open ticket");
        assignedOpen.setDescription("desc");
        assignedOpen.setStatus(TicketStatus.OPEN);
        assignedOpen.setAssignedTo(savedUser);
        Ticket savedAssignedOpen = ticketRepository.save(assignedOpen);

        Ticket assignedInProgress = new Ticket();
        assignedInProgress.setTitle("Assigned in-progress ticket");
        assignedInProgress.setDescription("desc");
        assignedInProgress.setStatus(TicketStatus.IN_PROGRESS);
        assignedInProgress.setAssignedTo(savedUser);
        Ticket savedAssignedInProgress = ticketRepository.save(assignedInProgress);

        Page<AdminTicketResponseDto> ticketsPage = adminUserService.getUserTickets(savedUser.getId(), 0, 10);
        assertEquals(3, ticketsPage.getTotalElements());
        assertTrue(ticketsPage.getContent().stream().anyMatch(t -> t.getId().equals(savedCreated.getId())));
        assertTrue(ticketsPage.getContent().stream().anyMatch(t -> t.getId().equals(savedAssignedOpen.getId())));
        assertTrue(ticketsPage.getContent().stream().anyMatch(t -> t.getId().equals(savedAssignedInProgress.getId())));

        UserStatsDto stats = adminUserService.getUserStats(savedUser.getId());
        assertEquals(1, stats.getCreatedCount());
        assertEquals(2, stats.getAssignedCount());
        assertEquals(1, stats.getOpenCount());
        assertEquals(1, stats.getInProgressCount());
        assertEquals(1, stats.getResolvedCount());
        assertEquals(0, stats.getClosedCount());
        assertEquals(0, stats.getCancelledCount());
    }
}
