package com.yazilimxyz.enterprise_ticket_system.mapper;

import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminTicketResponseDto;
import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import org.springframework.stereotype.Component;

@Component
public class AdminTicketMapper {

    public AdminTicketResponseDto toDto(Ticket t) {
        if (t == null) {
            return null;
        }

        AdminTicketResponseDto dto = new AdminTicketResponseDto();
        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setDescription(t.getDescription());

        // Sende status / priority String kolonlar (varchar) – enum değil:
        dto.setStatus(t.getStatus());
        dto.setPriority(t.getPriority());

        // Şema: tickets tablosunda owner yok, sadece:
        // assigned_user_id, assigned_by_admin_id var.
        // DTO’daki owner alanlarını şimdilik null bırakıyoruz.
        dto.setOwnerId(null);
        dto.setOwnerEmail(null);

        // assignedUser ilişkisinden dolduralım
        User assigned = t.getAssignedTo(); // entity'de alan adın farklıysa burayı değiştir.
        if (assigned != null) {
            dto.setAssignedToId(assigned.getId());
            dto.setAssignedToEmail(assigned.getEmail());
        }

        dto.setCreatedAt(t.getCreatedAt());
        dto.setUpdatedAt(t.getUpdatedAt());

        return dto;
    }
}
