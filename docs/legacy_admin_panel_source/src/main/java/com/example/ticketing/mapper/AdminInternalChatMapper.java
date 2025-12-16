package com.example.ticketing.mapper;

import com.example.ticketing.dto.admin.AdminInternalChatMessageDto;
import com.example.ticketing.entity.InternalChat;
import com.example.ticketing.entity.User;

public final class AdminInternalChatMapper {

    private AdminInternalChatMapper() {
    }

    public static AdminInternalChatMessageDto toDto(InternalChat chat) {
        if (chat == null) {
            return null;
        }
        User author = chat.getAuthor();
        return AdminInternalChatMessageDto.builder()
                .id(chat.getId())
                .ticketId(chat.getTicket() != null ? chat.getTicket().getId() : null)
                .authorId(author != null ? author.getId() : null)
                .authorName(author != null ? author.getFullName() : null)
                .message(chat.getMessage())
                .createdAt(chat.getCreatedAt())
                .build();
    }
}
