package com.yazilimxyz.enterprise_ticket_system.dto.admin;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponseDto {
    private Long id;
    private String email;
    private String name;
    private String surname;
    private String role;
    private boolean active;
    private boolean approved;
    private String department;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
