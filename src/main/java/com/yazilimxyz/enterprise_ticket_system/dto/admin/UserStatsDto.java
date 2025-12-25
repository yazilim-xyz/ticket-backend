package com.yazilimxyz.enterprise_ticket_system.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDto {
    private long createdCount;
    private long assignedCount;
    private long openCount;
    private long inProgressCount;
    private long resolvedCount;
    private long closedCount;
}
