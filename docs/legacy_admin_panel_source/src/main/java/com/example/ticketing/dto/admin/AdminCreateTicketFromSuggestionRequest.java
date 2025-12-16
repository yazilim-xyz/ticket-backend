package com.example.ticketing.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminCreateTicketFromSuggestionRequest {

    @NotNull
    private Long creatorUserId;

    private Long assigneeUserId;
}
