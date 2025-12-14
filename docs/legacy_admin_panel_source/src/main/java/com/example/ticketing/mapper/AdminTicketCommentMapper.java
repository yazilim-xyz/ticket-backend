package com.example.ticketing.mapper;

import com.example.ticketing.dto.admin.AdminTicketCommentDto;
import com.example.ticketing.entity.TicketComment;
import com.example.ticketing.entity.User;

public final class AdminTicketCommentMapper {

    private AdminTicketCommentMapper() {
    }

    public static AdminTicketCommentDto toDto(TicketComment comment) {
        if (comment == null) {
            return null;
        }
        User author = comment.getAuthor();
        return AdminTicketCommentDto.builder()
                .id(comment.getId())
                .ticketId(comment.getTicket() != null ? comment.getTicket().getId() : null)
                .authorId(author != null ? author.getId() : null)
                .authorName(author != null ? author.getFullName() : null)
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
