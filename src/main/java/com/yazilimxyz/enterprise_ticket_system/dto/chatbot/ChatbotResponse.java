package com.yazilimxyz.enterprise_ticket_system.dto.chatbot;

/**
 * Chatbot yanıtı için kullanılan response DTO
 */
public record ChatbotResponse(
        String message,
        String response) {
}
