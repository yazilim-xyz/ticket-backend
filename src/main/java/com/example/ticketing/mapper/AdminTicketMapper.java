package com.example.ticketing.mapper;

import com.example.ticketing.dto.admin.AdminCreateTicketRequest;
import com.example.ticketing.dto.admin.AdminTicketDto;
import com.example.ticketing.dto.admin.AdminUpdateTicketRequest;
import com.example.ticketing.entity.Ticket;
import com.example.ticketing.entity.TicketPriority;
import com.example.ticketing.entity.TicketStatus;
import com.example.ticketing.entity.User;

public final class AdminTicketMapper {

    private AdminTicketMapper() {
    }

    public static AdminTicketDto toDto(Ticket ticket) {
        if (ticket == null) {
            return null;
        }
        User created = ticket.getCreatedBy();
        User assigned = ticket.getAssignedTo();
        return AdminTicketDto.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .createdById(created != null ? created.getId() : null)
                .createdByName(created != null ? created.getFullName() : null)
                .assignedToId(assigned != null ? assigned.getId() : null)
                .assignedToName(assigned != null ? assigned.getFullName() : null)
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }

    public static Ticket fromCreateRequest(AdminCreateTicketRequest request, User creator, User assignee) {
        return Ticket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(TicketStatus.OPEN)
                .createdBy(creator)
                .assignedTo(assignee)
                .build();
    }

    public static void applyUpdate(Ticket ticket, AdminUpdateTicketRequest request, User assignee) {
        if (request.getTitle() != null) {
            ticket.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            ticket.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            ticket.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            ticket.setStatus(request.getStatus());
        }
        if (request.getAssignedToId() != null) {
            ticket.setAssignedTo(assignee);
        }
    }

    public static void applyAssignment(Ticket ticket, User assignee) {
        ticket.setAssignedTo(assignee);
    }

    public static void applyStatus(Ticket ticket, TicketStatus status) {
        ticket.setStatus(status);
    }
}
