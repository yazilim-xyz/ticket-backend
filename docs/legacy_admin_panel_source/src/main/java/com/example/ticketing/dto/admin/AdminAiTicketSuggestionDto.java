package com.example.ticketing.dto.admin;

import com.example.ticketing.entity.AiSuggestionStatus;
import com.example.ticketing.entity.TicketPriority;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AdminAiTicketSuggestionDto {
    private Long id;
    private String suggestedTitle;
    private String suggestedDescription;
    private TicketPriority priorityGuess;
    private AiSuggestionStatus status;
    private Long createdById;
    private String createdByName;
    private Instant createdAt;
}
