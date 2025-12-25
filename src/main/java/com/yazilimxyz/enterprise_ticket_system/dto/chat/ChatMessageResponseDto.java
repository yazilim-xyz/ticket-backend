package com.yazilimxyz.enterprise_ticket_system.dto.chat;

import java.time.OffsetDateTime;

public record ChatMessageResponseDto(
                Long id,
                Long senderId,
                String senderName,
                Long receiverId,
                String receiverName,
                String message,
                OffsetDateTime createdAt) {
}
