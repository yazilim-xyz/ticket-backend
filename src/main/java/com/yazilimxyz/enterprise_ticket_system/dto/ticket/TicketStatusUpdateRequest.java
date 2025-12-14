package com.yazilimxyz.enterprise_ticket_system.dto.ticket;

import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;

public class TicketStatusUpdateRequest {

    private TicketStatus status;

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}
