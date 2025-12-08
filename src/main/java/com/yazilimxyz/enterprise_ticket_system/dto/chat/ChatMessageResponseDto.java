package com.yazilimxyz.enterprise_ticket_system.dto.chat;

import java.time.LocalDateTime;

public record ChatMessageResponseDto(
        Long id,
        Long senderId,
        String senderName,
        Long receiverId,
        String receiverName,
        String message,
        LocalDateTime createdAt) {
}
