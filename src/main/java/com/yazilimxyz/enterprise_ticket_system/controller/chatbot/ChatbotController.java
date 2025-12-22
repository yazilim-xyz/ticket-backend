package com.yazilimxyz.enterprise_ticket_system.controller.chatbot;

import com.yazilimxyz.enterprise_ticket_system.dto.chatbot.ChatbotRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.chatbot.ChatbotResponse;
import com.yazilimxyz.enterprise_ticket_system.service.chatbot.ChatbotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@Slf4j
public class ChatbotController {

    private final ChatbotService chatbotService;

    /**
     * Kullanıcıdan mesaj alır, Gemini API'ye gönderir ve yanıtı döner
     * 
     * Content-Type: application/json
     * 
     * Request body örneği:
     * {
     * "message": "Merhaba, destek ekibi hakkında bilgi verir misin?"
     * }
     * 
     * Response örneği:
     * {
     * "message": "Merhaba, destek ekibi hakkında bilgi verir misin?",
     * "response": "Gemini'nin yanıtı buraya gelir..."
     * }
     */
    @PostMapping("/ask")
    public ResponseEntity<ChatbotResponse> askChatbot(
            @Valid @RequestBody ChatbotRequest request,
            Authentication authentication) {

        log.info("📨 Chatbot isteği alındı. Kullanıcı: {}",
                authentication != null ? authentication.getName() : "ANONYMOUS");
        log.debug("Mesaj: {}", request.message());

        // Mono'yu block() ile bekle ve sync response dön
        String geminiResponse = chatbotService.getChatbotResponse(request.message())
                .doOnSuccess(resp -> log.info("✅ Gemini yanıtı alındı: {}",
                        resp.substring(0, Math.min(50, resp.length()))))
                .block(); // ← Burada reactive stream'i blocking yap

        ChatbotResponse response = new ChatbotResponse(request.message(), geminiResponse);
        log.info("✅ Response döndürülüyor. Status: 200");

        return ResponseEntity.ok(response);
    }
}
