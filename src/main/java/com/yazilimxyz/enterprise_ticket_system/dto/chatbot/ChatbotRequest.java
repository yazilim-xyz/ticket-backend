package com.yazilimxyz.enterprise_ticket_system.dto.chatbot;

import jakarta.validation.constraints.NotBlank;

/**
 * Chatbot'a soru sormak için kullanılan request DTO
 */
public record ChatbotRequest(
        @NotBlank(message = "Mesaj boş olamaz") String message) {
}
