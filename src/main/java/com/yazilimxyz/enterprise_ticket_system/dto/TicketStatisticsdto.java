package com.yazilimxyz.enterprise_ticket_system.dto;

import java.time.OffsetDateTime;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketPriority;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;
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
    private Long id;
    private String title;
    private TicketStatus status;
    private TicketPriority priority;
    private OffsetDateTime createdAt;
    private OffsetDateTime dueDate;
}
