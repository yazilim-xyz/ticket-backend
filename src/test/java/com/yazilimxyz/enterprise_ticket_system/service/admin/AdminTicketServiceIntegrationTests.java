package com.yazilimxyz.enterprise_ticket_system.service.admin;

import com.yazilimxyz.enterprise_ticket_system.Repositories.TicketRepository;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminTicketCreateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminTicketResponseDto;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminTicketUpdateRequest;
import com.yazilimxyz.enterprise_ticket_system.entities.Role;
import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketCategory;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketPriority;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminTicketServiceIntegrationTests {

    @Autowired
    private AdminTicketService adminTicketService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void createUpdateDeleteRestoreTicket() {
        User user = new User();
        user.setFullName("Admin Ticket User");
        user.setEmail("adm-ticket-user+" + System.currentTimeMillis() + "@local");
        user.setPasswordHash("hash");
        user.setRole(Role.ADMIN);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        AdminTicketCreateRequest createReq = new AdminTicketCreateRequest();
        createReq.setTitle("Admin created ticket");
        createReq.setDescription("Initial description");
        createReq.setPriority(TicketPriority.HIGH);
        createReq.setCategory(TicketCategory.BUG);
        createReq.setCreatedByUserId(savedUser.getId());
        createReq.setAssignedToUserId(savedUser.getId());
        createReq.setDueDate(OffsetDateTime.now(ZoneOffset.UTC).plusDays(3));

        AdminTicketResponseDto created = adminTicketService.createTicket(createReq);
        assertNotNull(created.getId());
        assertEquals("Admin created ticket", created.getTitle());
        assertEquals(TicketPriority.HIGH, created.getPriority());

        AdminTicketUpdateRequest updateReq = new AdminTicketUpdateRequest();
        updateReq.setTitle("Updated title");
        updateReq.setDescription("Updated description");
        updateReq.setPriority(TicketPriority.MEDIUM);
        updateReq.setCategory(TicketCategory.OTHER);
        updateReq.setDueDate(OffsetDateTime.now(ZoneOffset.UTC).plusDays(5));

        AdminTicketResponseDto updated = adminTicketService.updateTicket(created.getId(), updateReq);
        assertEquals("Updated title", updated.getTitle());
        assertEquals("Updated description", updated.getDescription());
        assertEquals(TicketPriority.MEDIUM, updated.getPriority());

        adminTicketService.deleteTicket(created.getId());
        Ticket deleted = ticketRepository.findById(created.getId()).orElseThrow();
        assertTrue(deleted.getIsDeleted());

        adminTicketService.restoreTicket(created.getId());
        Ticket restored = ticketRepository.findById(created.getId()).orElseThrow();
        assertFalse(restored.getIsDeleted());
    }
}
