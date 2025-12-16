package com.example.ticketing.mapper;

import com.example.ticketing.dto.admin.AdminTicketActivityLogDto;
import com.example.ticketing.entity.TicketActivityLog;
import com.example.ticketing.entity.User;

public final class AdminTicketActivityLogMapper {

    private AdminTicketActivityLogMapper() {
    }

    public static AdminTicketActivityLogDto toDto(TicketActivityLog log) {
        if (log == null) {
            return null;
        }
        User performer = log.getPerformedBy();
        return AdminTicketActivityLogDto.builder()
                .id(log.getId())
                .ticketId(log.getTicket() != null ? log.getTicket().getId() : null)
                .performedById(performer != null ? performer.getId() : null)
                .performedByName(performer != null ? performer.getFullName() : null)
                .action(log.getAction())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
