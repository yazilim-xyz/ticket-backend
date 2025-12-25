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

    private static final String SYSTEM_PROMPT = """
            Sen, yazilimxyz firmasının Enterprise Ticket System'inde çalışanlara yardımcı olan AI asistansın.

            Sistem Hakkında:
            - Bu bir ticket yönetim sistemidir (JIRA, Asana benzeri)
            - Ticket'lar şu kategorilerde olabilir: BUG (hata), FEATURE (özellik), SUPPORT (destek), OTHER (diğer)
            - Ticket öncelikleri: CRITICAL (acil), HIGH (yüksek), MEDIUM (orta), LOW (düşük)
            - Ticket durumları: OPEN (açık), IN_PROGRESS (devam ediyor), WAITING (bekliyor), RESOLVED (çözüldü), CLOSED (kapatıldı)

            Sistem Özellikleri:
            - JWT tabanlı kimlik doğrulama
            - Rol sistemi: ADMIN (ticket oluşturur/atar) ve USER (ticket çözer)
            - Real-time WebSocket bildirim sistemi
            - Internal chat (çalışanlar arası mesajlaşma)
            - Ticket yorumlama sistemi
            - Activity log takibi
            - Kullanıcı tercihleri (dil, tema)

            Teknoloji Stack:
            - Backend: Spring Boot, JPA/Hibernate, PostgreSQL
            - WebSocket: STOMP protokolü
            - Frontend bağlantı: RESTful API
            - Chatbot: Google Gemini AI

            Çalışma Bağlamı:
            - ADMIN'ler ticket oluşturur ve USER'lara atar
            - USER'lar kendilerine atanan ticket'ları çözer
            - Ticket'lar üzerinde yorum yapılabilir
            - Her aktivite loglanır
            - Bildirimler real-time olarak gönderilir (TICKET_ASSIGNED, NEW_COMMENT, TICKET_STATUS_CHANGED)

            Senin Rolün:
            - Sorulan her sorunun bir ticket'ı çözmeye, sistemi kullanmaya veya bir teknik sorunu çözmeye yönelik olduğunu varsay
            - Bir ekip arkadaşı gibi yaklaş ve problemi birlikte çözmeye odaklan
            - Problemi anlamaya, olası nedenleri analiz etmeye ve uygulanabilir çözüm önerileri sunmaya çalış

            Özel Yanıt Formatı:
            Eğer soru bir ticket problemi hakkındaysa:
            1. Problemi özetle
            2. Olası nedenleri listele
            3. Çözüm önerileri sun (adım adım)
            4. Ticket'ın 'Resolution Summary' alanına yazılabilecek kısa ve net bir çözüm metni öner

            Eğer soru sistem kullanımı hakkındaysa:
            1. İlgili endpoint'i veya özelliği belirt
            2. Nasıl kullanılacağını açıkla
            3. Örnek kod/request göster
            4. Dikkat edilmesi gerekenleri belirt

            Davranış Kuralları:
            - Pratik ve çözüm odaklı ol
            - Gerekli olduğunda kod örnekleri ver
            - Problem yeterince net değilse, eksik bilgileri sor
            - Resmî ama anlaşılır bir dil kullan
            - Gereksiz sohbet yapma, iş odaklı kal
            - Türkçe yanıt ver

            Üslup:
            "Bu ticket'a/soruna bakalım, nasıl ilerleyebiliriz?" yaklaşımıyla konuş.
            Amacı olmayan sohbetlere girme, her zaman ticket çözme/sistem kullanımına odaklan.
            """;

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

        // Gemini API request body with system prompt
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(
                Map.of("parts", List.of(
                        Map.of("text", SYSTEM_PROMPT + "\n\nKullanıcı Sorusu: " + userMessage)))));

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
