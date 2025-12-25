package com.yazilimxyz.enterprise_ticket_system.bootstrap;

import com.yazilimxyz.enterprise_ticket_system.Repositories.TicketActivityLogRepository;
import com.yazilimxyz.enterprise_ticket_system.Repositories.TicketRepository;
import com.yazilimxyz.enterprise_ticket_system.entities.Role;
import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;
import com.yazilimxyz.enterprise_ticket_system.entities.TicketActivityLog;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketCategory;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketPriority;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;
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
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2) // Kullanıcılardan sonra
public class TicketSeeder implements CommandLineRunner {

        private final TicketRepository ticketRepository;
        private final UserRepository userRepository;
        private final TicketActivityLogRepository activityLogRepository;

        @Value("${app.seed.enabled:true}")
        private boolean enabled;

        private final Random random = new Random();

        @Override
        public void run(String... args) {
                if (!enabled) {
                        log.debug("[TicketSeeder] Seeding disabled");
                        return;
                }

                if (ticketRepository.count() > 0) {
                        log.debug("[TicketSeeder] Tickets already seeded");
                        return;
                }

                List<User> allUsers = userRepository.findAll();
                if (allUsers.size() < 5) {
                        log.warn("[TicketSeeder] Not enough users to create tickets");
                        return;
                }

                // ADMIN'ler (ticket oluşturur)
                List<User> admins = allUsers.stream()
                                .filter(u -> u.getRole() == Role.ADMIN)
                                .toList();

                // USER'lar (ticket'lara assign edilir, çözerler)
                List<User> users = allUsers.stream()
                                .filter(u -> u.getRole() == Role.USER)
                                .toList();

                List<Ticket> tickets = new ArrayList<>();
                OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

                // BUG Tickets (ADMIN oluşturur, USER'a atar)
                tickets.add(createTicket(
                                "Login sayfasında 500 hatası alıyorum",
                                "Kullanıcı adı ve şifre girip login butonuna bastığımda sürekli 500 Internal Server Error alıyorum. Chrome tarayıcı kullanıyorum. Konsola baktığımda CORS hatası görünüyor.",
                                TicketStatus.RESOLVED,
                                TicketPriority.CRITICAL,
                                TicketCategory.BUG,
                                getRandomUser(admins),
                                getRandomUser(users),
                                now.minusDays(15),
                                now.minusDays(14),
                                "CORS ayarları güncellenerek düzeltildi. Allowed origins listesine production domain eklendi."));

                tickets.add(createTicket(
                                "Dashboard sayfası yüklenmiyor",
                                "Ana sayfaya giriş yaptıktan sonra dashboard sayfası beyaz ekran kalıyor. Network sekmesinde bakınca API'den veri gelmiyor.",
                                TicketStatus.CLOSED,
                                TicketPriority.HIGH,
                                TicketCategory.BUG,
                                getRandomUser(admins),
                                getRandomUser(users),
                                now.minusDays(20),
                                now.minusDays(18),
                                "Backend API endpoint'i güncellendi. Frontend cache temizlendi."));

                tickets.add(createTicket(
                                "Email bildirimleri gelmiyor",
                                "Yeni ticket açıldığında email bildirimi almam gerekiyor ama hiç gelmiyor. Spam klasörüne de bakmadım.",
                                TicketStatus.IN_PROGRESS,
                                TicketPriority.MEDIUM,
                                TicketCategory.BUG,
                                getRandomUser(admins),
                                getRandomUser(users),
                                now.minusDays(5),
                                now.plusDays(2),
                                null));

                tickets.add(createTicket(
                                "Ticket filtre butonu çalışmıyor",
                                "Tickets sayfasında status'e göre filtreleme yapmaya çalışıyorum ama filter butonu hiçbir şey yapmıyor. Konsola da hata düşmüyor.",
                                TicketStatus.OPEN,
                                TicketPriority.LOW,
                                TicketCategory.BUG,
                                getRandomUser(admins),
                                null,
                                now.minusDays(2),
                                now.plusDays(5),
                                null));

                tickets.add(createTicket(
                                "Profil fotoğrafı yükleyemiyorum",
                                "Profil ayarlarından fotoğraf yüklemeye çalıştığımda 'File too large' hatası alıyorum ama dosya sadece 500KB.",
                                TicketStatus.OPEN,
                                TicketPriority.MEDIUM,
                                TicketCategory.BUG,
                                getRandomUser(admins),
                                getRandomUser(users),
                                now.minusDays(1),
                                now.plusDays(3),
                                null));

                // FEATURE Tickets
                tickets.add(createTicket(
                                "Dark mode özelliği eklensin",
                                "Uygulama çok parlak, özellikle gece kullanırken gözlerimi yoruyor. Dark mode seçeneği olsa harika olur.",
                                TicketStatus.IN_PROGRESS,
                                TicketPriority.LOW,
                                TicketCategory.FEATURE,
                                getRandomUser(admins),
                                getRandomUser(users),
                                now.minusDays(30),
                                now.plusDays(15),
                                null));

                tickets.add(createTicket(
                                "Ticket'lara dosya ekleme özelliği",
                                "Ticket açarken ekran görüntüsü veya log dosyası ekleyebilmek istiyorum. Şu an sadece yazı yazabiliyorum.",
                                TicketStatus.OPEN,
                                TicketPriority.HIGH,
                                TicketCategory.FEATURE,
                                getRandomUser(admins),
                                null,
                                now.minusDays(10),
                                now.plusDays(20),
                                null));

                tickets.add(createTicket(
                                "Excel export özelliği",
                                "Tüm ticket'ları Excel formatında dışa aktarabilmek istiyorum. Raporlama için gerekli.",
                                TicketStatus.OPEN,
                                TicketPriority.MEDIUM,
                                TicketCategory.FEATURE,
                                getRandomUser(admins),
                                null,
                                now.minusDays(7),
                                null,
                                null));

                tickets.add(createTicket(
                                "Mobil uygulama geliştirilmeli",
                                "Telefondan ticket takibi yapmak istiyorum. Responsive web var ama native mobil uygulama daha iyi olur.",
                                TicketStatus.OPEN,
                                TicketPriority.LOW,
                                TicketCategory.FEATURE,
                                getRandomUser(admins),
                                null,
                                now.minusDays(25),
                                null,
                                null));

                tickets.add(createTicket(
                                "Ticket template'leri oluşturma",
                                "Sık kullanılan ticket türleri için hazır şablonlar oluşturabilmek istiyorum.",
                                TicketStatus.RESOLVED,
                                TicketPriority.MEDIUM,
                                TicketCategory.FEATURE,
                                getRandomUser(admins),
                                getRandomUser(users),
                                now.minusDays(40),
                                now.minusDays(30),
                                "Template sistemi başarıyla eklendi. Admin panelinden yönetilebiliyor."));

                // SUPPORT Tickets
                tickets.add(createTicket(
                                "Şifremi nasıl değiştirebilirim?",
                                "Profil sayfasında şifre değiştirme seçeneği bulamıyorum. Yardımcı olabilir misiniz?",
                                TicketStatus.RESOLVED,
                                TicketPriority.LOW,
                                TicketCategory.SUPPORT,
                                getRandomUser(admins),
                                getRandomUser(users),
                                now.minusDays(3),
                                now.minusDays(3),
                                "Profil ayarları -> Security -> Change Password yolunu takip edebilirsiniz."));

                tickets.add(createTicket(
                                "Hesabım askıya alınmış görünüyor",
                                "Login olmaya çalışıyorum ama 'Account is inactive' hatası alıyorum. Hesabım neden kapatıldı?",
                                TicketStatus.CLOSED,
                                TicketPriority.CRITICAL,
                                TicketCategory.SUPPORT,
                                getRandomUser(admins),
                                getRandomUser(users),
                                now.minusDays(8),
                                now.minusDays(7),
                                "Hesabınız 90 gün hareketsizlik nedeniyle otomatik kapatılmış. Yeniden aktif hale getirildi."));

                tickets.add(createTicket(
                                "Bildirim ayarlarını nasıl kapatırım?",
                                "Email bildirimleri çok fazla geliyor, bunları azaltmak veya kapatmak istiyorum.",
                                TicketStatus.IN_PROGRESS,
                                TicketPriority.LOW,
                                TicketCategory.SUPPORT,
                                getRandomUser(admins),
                                getRandomUser(users),
                                now.minusDays(1),
                                now.plusDays(1),
                                null));

                tickets.add(createTicket(
                                "Ticket önceliği nasıl belirlenir?",
                                "Ticket açarken Priority seçeneği var ama hangisini seçeceğimi bilmiyorum. Bir rehber var mı?",
                                TicketStatus.RESOLVED,
                                TicketPriority.LOW,
                                TicketCategory.SUPPORT,
                                getRandomUser(admins),
                                getRandomUser(users),
                                now.minusDays(12),
                                now.minusDays(12),
                                "Documentation'da priority seçim rehberi eklendi. LOW: Günler, MEDIUM: Saatler, HIGH: Dakikalar, CRITICAL: Anında."));

                tickets.add(createTicket(
                                "API dokümantasyonuna nereden ulaşabilirim?",
                                "Entegrasyon yapmak için API dokümantasyonu lazım. Swagger linki nedir?",
                                TicketStatus.RESOLVED,
                                TicketPriority.MEDIUM,
                                TicketCategory.SUPPORT,
                                getRandomUser(admins),
                                getRandomUser(users),
                                now.minusDays(6),
                                now.minusDays(6),
                                "Swagger UI: http://localhost:8080/swagger-ui.html linki mail olarak gönderildi."));

                // OTHER Tickets
                tickets.add(createTicket(
                                "Sistem bakım tarihi ne zaman?",
                                "Planlı bir sistem bakımı var mı? Önceden bilmek istiyorum.",
                                TicketStatus.CLOSED,
                                TicketPriority.LOW,
                                TicketCategory.OTHER,
                                getRandomUser(admins),
                                getRandomUser(users),
                                now.minusDays(5),
                                now.minusDays(4),
                                "Önümüzdeki Cumartesi saat 02:00-04:00 arası planlı bakım yapılacak."));

                tickets.add(createTicket(
                                "Yeni çalışan için hesap açılması",
                                "Departmanımıza yeni katılan arkadaş için sistem hesabı açılması gerekiyor. İsim: Ayşegül Yıldırım, Email: aysegul.yildirim@enterprise.com",
                                TicketStatus.RESOLVED,
                                TicketPriority.MEDIUM,
                                TicketCategory.OTHER,
                                getRandomUser(admins),
                                getRandomUser(users),
                                now.minusDays(9),
                                now.minusDays(8),
                                "Hesap oluşturuldu ve aktivasyon maili gönderildi."));

                tickets.add(createTicket(
                                "Sistem performans raporu",
                                "Son bir aydaki sistem kullanım istatistiklerini görebilir miyim?",
                                TicketStatus.IN_PROGRESS,
                                TicketPriority.LOW,
                                TicketCategory.OTHER,
                                getRandomUser(admins),
                                getRandomUser(users),
                                now.minusDays(2),
                                now.plusDays(3),
                                null));

                tickets.add(createTicket(
                                "Eski ticket'ları arşivleme",
                                "6 aydan eski kapalı ticket'lar arşivlenebilir mi? Liste çok kalabalık.",
                                TicketStatus.OPEN,
                                TicketPriority.LOW,
                                TicketCategory.OTHER,
                                getRandomUser(admins),
                                null,
                                now.minusDays(4),
                                null,
                                null));

                tickets.add(createTicket(
                                "Eğitim videoları talep ediyorum",
                                "Yeni kullanıcılar için sistem kullanım eğitim videoları hazırlanabilir mi?",
                                TicketStatus.OPEN,
                                TicketPriority.MEDIUM,
                                TicketCategory.OTHER,
                                getRandomUser(admins),
                                null,
                                now.minusDays(50),
                                null,
                                null));

                // Son günlerdeki aktif ticket'lar
                tickets.add(createTicket(
                                "WebSocket bağlantısı kopuyor",
                                "Real-time notification sisteminde sürekli bağlantı kopması yaşıyorum. Her 5 dakikada bir yeniden bağlanıyor.",
                                TicketStatus.OPEN,
                                TicketPriority.HIGH,
                                TicketCategory.BUG,
                                getRandomUser(admins),
                                getRandomUser(users),
                                now.minusHours(6),
                                now.plusDays(2),
                                null));

                tickets.add(createTicket(
                                "Chatbot yanıt vermiyor",
                                "AI chatbot'a soru soruyorum ama yanıt vermiyor. Loading ikonu sonsuza kadar dönüyor.",
                                TicketStatus.OPEN,
                                TicketPriority.CRITICAL,
                                TicketCategory.BUG,
                                getRandomUser(admins),
                                getRandomUser(users),
                                now.minusHours(2),
                                now.plusHours(24),
                                null));

                tickets.add(createTicket(
                                "Raporlama modülü eklenmeli",
                                "Ticket istatistikleri için grafik ve rapor modülü olsa harika olur. Chart.js veya benzer bir kütüphane kullanılabilir.",
                                TicketStatus.OPEN,
                                TicketPriority.MEDIUM,
                                TicketCategory.FEATURE,
                                getRandomUser(admins),
                                null,
                                now.minusHours(12),
                                null,
                                null));

                ticketRepository.saveAll(tickets);
                log.info("[TicketSeeder] Successfully seeded {} tickets", tickets.size());

                // Activity logs oluştur
                List<TicketActivityLog> activityLogs = new ArrayList<>();
                for (Ticket ticket : tickets) {
                        // Ticket oluşturma logu
                        activityLogs.add(createActivityLog(
                                        ticket,
                                        ticket.getCreatedBy(),
                                        "TICKET_CREATED",
                                        String.format("Ticket created: %s [%s/%s]", ticket.getTitle(),
                                                        ticket.getPriority(), ticket.getCategory()),
                                        ticket.getCreatedAt()));

                        // Atama yapıldıysa
                        if (ticket.getAssignedTo() != null) {
                                activityLogs.add(createActivityLog(
                                                ticket,
                                                ticket.getCreatedBy(),
                                                "TICKET_ASSIGNED",
                                                String.format("Ticket assigned to %s %s",
                                                                ticket.getAssignedTo().getName(),
                                                                ticket.getAssignedTo().getSurname()),
                                                ticket.getCreatedAt().plusMinutes(30)));
                        }

                        // Status değişiklikleri
                        if (ticket.getStatus() != TicketStatus.OPEN) {
                                activityLogs.add(createActivityLog(
                                                ticket,
                                                ticket.getAssignedTo() != null ? ticket.getAssignedTo()
                                                                : ticket.getCreatedBy(),
                                                "STATUS_CHANGED",
                                                String.format("Status changed to %s", ticket.getStatus()),
                                                ticket.getUpdatedAt()));
                        }

                        // Resolution eklendiyse
                        if (ticket.getResolutionSummary() != null) {
                                activityLogs.add(createActivityLog(
                                                ticket,
                                                ticket.getAssignedTo() != null ? ticket.getAssignedTo()
                                                                : ticket.getCreatedBy(),
                                                "RESOLUTION_ADDED",
                                                "Resolution summary added",
                                                ticket.getUpdatedAt().plusMinutes(5)));
                        }
                }

                activityLogRepository.saveAll(activityLogs);
                log.info("[TicketSeeder] Successfully seeded {} activity logs", activityLogs.size());
        }

        private Ticket createTicket(String title, String description, TicketStatus status,
                        TicketPriority priority, TicketCategory category,
                        User createdBy, User assignedTo,
                        OffsetDateTime createdAt, OffsetDateTime dueDate,
                        String resolutionSummary) {
                Ticket ticket = new Ticket();
                ticket.setTitle(title);
                ticket.setDescription(description);
                ticket.setStatus(status);
                ticket.setPriority(priority);
                ticket.setCategory(category);
                ticket.setCreatedBy(createdBy);
                ticket.setAssignedTo(assignedTo);
                ticket.setCreatedAt(createdAt);
                ticket.setUpdatedAt(createdAt);
                ticket.setDueDate(dueDate);
                ticket.setResolutionSummary(resolutionSummary);
                ticket.setIsDeleted(false);
                return ticket;
        }

        private User getRandomUser(List<User> users) {
                if (users.isEmpty())
                        return null;
                return users.get(random.nextInt(users.size()));
        }

        private TicketActivityLog createActivityLog(Ticket ticket, User user, String actionType,
                        String actionDetails, OffsetDateTime createdAt) {
                TicketActivityLog log = new TicketActivityLog();
                log.setTicket(ticket);
                log.setUser(user);
                log.setActionType(actionType);
                log.setActionDetails(actionDetails);
                log.setCreatedAt(createdAt);
                return log;
        }
}
