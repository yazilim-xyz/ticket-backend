package com.yazilimxyz.enterprise_ticket_system.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatusSummaryDto {
    private long total;

    private long opened;
    private long inProgress;
    private long done;
    private long deleted;
}
