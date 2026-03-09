package com.yazilimxyz.enterprise_ticket_system.dto;

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
    private long open;
    private long waiting;
    private long inProgress;
    private long resolved;
    private long closed;
    
}
