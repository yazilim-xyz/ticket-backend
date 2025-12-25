package com.yazilimxyz.enterprise_ticket_system.dto.user;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListItemDto {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String role;
}
