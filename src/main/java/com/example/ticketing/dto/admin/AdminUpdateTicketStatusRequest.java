package com.example.ticketing.dto.admin;

import com.example.ticketing.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminUpdateTicketStatusRequest {

    @NotNull
    private TicketStatus status;
}
