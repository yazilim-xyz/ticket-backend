package com.example.ticketing.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminCreateTicketCommentRequest {

    private Long ticketId;

    @NotBlank
    private String content;
}
