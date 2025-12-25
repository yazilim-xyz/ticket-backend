package com.yazilimxyz.enterprise_ticket_system.dto;

import lombok.*;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatisticsdto {
    private long total;
    private long opened;
    private long inProgress;
    private long resolved;
    private long closed;
    private long overdue;
}
