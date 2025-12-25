package com.yazilimxyz.enterprise_ticket_system.bootstrap;

import com.yazilimxyz.enterprise_ticket_system.entities.InternalChat;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.repository.MessageRepository;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(6) // Kullanıcılardan sonra
public class InternalChatSeeder implements CommandLineRunner {

        private final MessageRepository messageRepository;
        private final UserRepository userRepository;

        @Value("${app.seed.enabled:true}")
        private boolean enabled;

        @Override
        public void run(String... args) {
                if (!enabled) {
                        log.debug("[InternalChatSeeder] Seeding disabled");
                        return;
                }

                if (messageRepository.count() > 0) {
                        log.debug("[InternalChatSeeder] Internal chats already seeded");
                        return;
                }

                List<User> users = userRepository.findAll();
                if (users.size() < 4) {
                        log.warn("[InternalChatSeeder] Not enough users for chat seeding");
                        return;
                }

                List<InternalChat> messages = new ArrayList<>();
                OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

                // Admin ve çalışan user'lar arasında konuşmalar
                User admin1 = users.stream()
                                .filter(u -> u.getEmail().contains("ahmet.yilmaz"))
                                .findFirst().orElse(users.get(0));

                User user1 = users.stream()
                                .filter(u -> u.getEmail().contains("mehmet.demir"))
                                .findFirst().orElse(users.get(1));

                User user2 = users.stream()
                                .filter(u -> u.getEmail().contains("zeynep.celik"))
                                .findFirst().orElse(users.get(2));

                User user3 = users.stream()
                                .filter(u -> u.getEmail().contains("selin.aydin"))
                                .findFirst().orElse(users.get(3));

                // Konuşma 1: Admin ve user arası (ticket koordinasyonu)
                messages.add(createMessage(
                                admin1, user1,
                                "Merhaba Mehmet, bugünkü kritik ticket'ları gözden geçirdin mi?",
                                now.minusDays(2).withHour(9).withMinute(15)));
                messages.add(createMessage(
                                user1, admin1,
                                "Günaydın, evet hepsine baktım. WebSocket sorunu öncelikli olarak ele alınmalı.",
                                now.minusDays(2).withHour(9).withMinute(18)));
                messages.add(createMessage(
                                admin1, user1,
                                "Haklısın. Zeynep ile birlikte çalışabilir misiniz?",
                                now.minusDays(2).withHour(9).withMinute(20)));
                messages.add(createMessage(
                                user1, admin1,
                                "Tabi, hemen koordine oluyoruz.",
                                now.minusDays(2).withHour(9).withMinute(22)));

                // Konuşma 2: İki user arası (işbirliği)
                messages.add(createMessage(
                                user1, user2,
                                "Zeynep, WebSocket timeout sorununda birlikte çalışalım mı?",
                                now.minusDays(2).withHour(10).withMinute(30)));
                messages.add(createMessage(
                                user2, user1,
                                "Tabii ki! Ben backend config'i kontrol ediyorum, sen frontend'e bakabilir misin?",
                                now.minusDays(2).withHour(10).withMinute(35)));
                messages.add(createMessage(
                                user1, user2,
                                "Tamam, heartbeat interval'i de kontrol edelim.",
                                now.minusDays(2).withHour(10).withMinute(40)));
                messages.add(createMessage(
                                user2, user1,
                                "İyi fikir. application.properties'te stomp heartbeat'i 20000ms olarak ayarlıyım mı?",
                                now.minusDays(2).withHour(11).withMinute(15)));
                messages.add(createMessage(
                                user1, user2,
                                "Evet, 20 saniye iyi olur. Ben de client tarafında bağlantı koptuğunda auto-reconnect ekleyeceğim.",
                                now.minusDays(2).withHour(11).withMinute(20)));

                // Konuşma 3: Admin ve user arası (ticket durumu)
                messages.add(createMessage(
                                admin1, user3,
                                "Merhaba Selin, ticket #23 ile ilgili ilerleme nasıl?",
                                now.minusDays(1).withHour(14).withMinute(30)));
                messages.add(createMessage(
                                user3, admin1,
                                "Merhaba, sorunu tespit ettim. Bugün içinde çözüm sağlayacağım.",
                                now.minusDays(1).withHour(14).withMinute(45)));
                messages.add(createMessage(
                                admin1, user3,
                                "Harika, öncelik veriyoruz. İhtiyacın olursa haber ver.",
                                now.minusDays(1).withHour(14).withMinute(48)));
                messages.add(createMessage(
                                user3, admin1,
                                "Teşekkürler, 2 saat içinde halledilir.",
                                now.minusDays(1).withHour(14).withMinute(50)));

                // Konuşma 4: Admin'ler arası
                if (users.stream().anyMatch(u -> u.getEmail().contains("elif.kaya"))) {
                        User admin2 = users.stream()
                                        .filter(u -> u.getEmail().contains("elif.kaya"))
                                        .findFirst().get();

                        messages.add(createMessage(
                                        admin1, admin2,
                                        "Elif, yeni feature request'ler için öncelik sırasını belirleyelim mi?",
                                        now.minusHours(5)));
                        messages.add(createMessage(
                                        admin2, admin1,
                                        "Evet, dark mode ve file upload en çok istenen özellikler. Önce bunlara odaklanalım.",
                                        now.minusHours(4).minusMinutes(55)));
                        messages.add(createMessage(
                                        admin1, admin2,
                                        "Anlaştık. Sprint planning'de bunları ilk sıraya alıyorum.",
                                        now.minusHours(4).minusMinutes(50)));
                }

                // Konuşma 5: User'lar arası teknik tartışma
                messages.add(createMessage(
                                user2, user1,
                                "Chatbot API'sinde sorun var, timeout alıyoruz. Senin de karşına çıktı mı?",
                                now.minusHours(3)));
                messages.add(createMessage(
                                user1, user2,
                                "Evet ben de fark ettim. API key'de mi sorun var acaba?",
                                now.minusHours(2).minusMinutes(55)));
                messages.add(createMessage(
                                user2, user1,
                                "Kontrol ettim, key geçerli. Sanırım rate limit'e takılıyoruz.",
                                now.minusHours(2).minusMinutes(50)));
                messages.add(createMessage(
                                user1, user2,
                                "Mantıklı. Premium plan'a geçmemiz gerekebilir. Admin'lere bildirelim.",
                                now.minusHours(2).minusMinutes(45)));

                // Konuşma 6: Admin'den user'a güncelleme
                messages.add(createMessage(
                                admin1, user2,
                                "Merhaba Zeynep, dark mode ne zaman yayına alınacak?",
                                now.minusMinutes(30)));
                messages.add(createMessage(
                                user2, admin1,
                                "Merhaba! Gelecek hafta test ortamında deneyebilirsiniz. Production'a 2 hafta içinde alıyoruz.",
                                now.minusMinutes(25)));

                messageRepository.saveAll(messages);
                log.info("[InternalChatSeeder] Successfully seeded {} internal chat messages", messages.size());
        }

        private InternalChat createMessage(User sender, User receiver, String message, OffsetDateTime createdAt) {
                InternalChat chat = new InternalChat();
                chat.setSender(sender);
                chat.setReceiver(receiver);
                chat.setMessage(message);
                chat.setCreatedAt(createdAt);
                return chat;
        }
}
