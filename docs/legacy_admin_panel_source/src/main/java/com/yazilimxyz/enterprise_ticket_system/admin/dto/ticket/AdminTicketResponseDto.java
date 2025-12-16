package com.yazilimxyz.enterprise_ticket_system.admin.dto.ticket;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminTicketResponseDto {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private Long ownerId;
    private String ownerEmail;
    private Long assignedToId;
    private String assignedToEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
