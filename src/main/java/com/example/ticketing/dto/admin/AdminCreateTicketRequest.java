package com.example.ticketing.dto.admin;

import com.example.ticketing.entity.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminCreateTicketRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private TicketPriority priority;

    @NotNull
    private Long createdById;

    private Long assignedToId;
}
