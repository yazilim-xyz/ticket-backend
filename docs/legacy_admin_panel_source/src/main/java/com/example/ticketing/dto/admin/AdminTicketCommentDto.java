package com.example.ticketing.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AdminTicketCommentDto {
    private Long id;
    private Long ticketId;
    private Long authorId;
    private String authorName;
    private String content;
    private Instant createdAt;
}
