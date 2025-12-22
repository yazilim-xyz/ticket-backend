package com.yazilimxyz.enterprise_ticket_system.service.chatbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    /**
     * Gemini API'ye istek gönderir ve yanıt alır
     */
    public Mono<String> getChatbotResponse(String userMessage) {
        WebClient webClient = webClientBuilder.build();
        // API key kontrolü
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-api-key-here")) {
            log.error("❌ GEMINI_API_KEY tanımlı değil veya geçersiz!");
            return Mono.just(
                    "Hata: Gemini API key tanımlı değil. Lütfen GEMINI_API_KEY environment variable'ını ayarlayın.");
        }

        log.info("🚀 Gemini API'ye istek gönderiliyor...");
        log.debug("API URL: {}", apiUrl);
        log.debug("API Key (ilk 10 karakter): {}...", apiKey.substring(0, Math.min(10, apiKey.length())));

        // Gemini API request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(
                Map.of("parts", List.of(
                        Map.of("text", userMessage)))));

        return webClient.post()
                .uri(apiUrl)
                .header("x-goog-api-key", apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("✅ Gemini API yanıt aldı"))
                .map(this::extractTextFromResponse)
                .onErrorResume(error -> {
                    if (error instanceof WebClientResponseException webEx) {
                        log.error("❌ Gemini API Hatası [{}]: {}", webEx.getStatusCode(),
                                webEx.getResponseBodyAsString());
                        log.error("Headers: {}", webEx.getHeaders());
                        return Mono.just("Hata: " + webEx.getStatusCode() + " - " + webEx.getResponseBodyAsString());
                    }
                    log.error("❌ Beklenmeyen hata: {}", error.getMessage(), error);
                    System.err.println("Gemini API hatası: " + error.getMessage());
                    return Mono.just("Üzgünüm, şu anda yanıt veremiyorum. Lütfen daha sonra tekrar deneyin.");
                });
    }

    /**
     * Gemini API'den gelen JSON yanıtından metni çıkarır
     */
    private String extractTextFromResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            return root.at("/candidates/0/content/parts/0/text").asText(
                    "Yanıt alınamadı.");
        } catch (Exception e) {
            System.err.println("JSON parse hatası: " + e.getMessage());
            return "Yanıt işlenirken hata oluştu.";
        }
    }
}
