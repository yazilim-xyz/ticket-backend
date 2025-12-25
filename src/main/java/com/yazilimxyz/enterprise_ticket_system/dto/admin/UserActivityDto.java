package com.yazilimxyz.enterprise_ticket_system.dto.admin;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityDto {
    private Long id;
    private Long ticketId;
    private String actionType;
    private String actionDetails;
    private OffsetDateTime createdAt;
}
