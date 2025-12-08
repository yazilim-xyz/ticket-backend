package com.example.ticketing.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminAssignTicketRequest {

    @NotNull
    private Long userId;
}
