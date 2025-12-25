package com.yazilimxyz.enterprise_ticket_system.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TicketResolutionUpdateRequest {
    @NotBlank(message = "Resolution summary is required")
    private String resolutionSummary;
}
