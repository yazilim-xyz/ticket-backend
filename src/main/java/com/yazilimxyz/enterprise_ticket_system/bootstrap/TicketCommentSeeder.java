package com.yazilimxyz.enterprise_ticket_system.bootstrap;

import com.yazilimxyz.enterprise_ticket_system.Repositories.TicketActivityLogRepository;
import com.yazilimxyz.enterprise_ticket_system.Repositories.TicketCommentRepository;
import com.yazilimxyz.enterprise_ticket_system.Repositories.TicketRepository;
import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;
import com.yazilimxyz.enterprise_ticket_system.entities.TicketActivityLog;
import com.yazilimxyz.enterprise_ticket_system.entities.TicketComment;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
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
@Order(3) // Ticket'lardan sonra
public class TicketCommentSeeder implements CommandLineRunner {

        private final TicketCommentRepository commentRepository;
        private final TicketRepository ticketRepository;
        private final UserRepository userRepository;
        private final TicketActivityLogRepository activityLogRepository;

        @Value("${app.seed.enabled:true}")
        private boolean enabled;

        @Override
        public void run(String... args) {
                if (!enabled) {
                        log.debug("[TicketCommentSeeder] Seeding disabled");
                        return;
                }

                if (commentRepository.count() > 0) {
                        log.debug("[TicketCommentSeeder] Comments already seeded");
                        return;
                }

                List<Ticket> tickets = ticketRepository.findAll();
                List<User> users = userRepository.findAll();

                if (tickets.isEmpty() || users.isEmpty()) {
                        log.warn("[TicketCommentSeeder] No tickets or users found");
                        return;
                }

                List<TicketComment> comments = new ArrayList<>();

                // Ticket 1 için yorumlar (Login 500 hatası - RESOLVED)
                Ticket ticket1 = tickets.stream()
                                .filter(t -> t.getTitle().contains("Login sayfasında"))
                                .findFirst().orElse(null);

                if (ticket1 != null) {
                        comments.add(createComment(
                                        ticket1,
                                        ticket1.getCreatedBy(),
                                        "Console'da tam hata mesajı şu: 'Access to XMLHttpRequest blocked by CORS policy'",
                                        ticket1.getCreatedAt().plusHours(1)));
                        comments.add(createComment(
                                        ticket1,
                                        ticket1.getAssignedTo(),
                                        "Sorunu tespit ettim, CORS ayarlarını kontrol ediyorum.",
                                        ticket1.getCreatedAt().plusHours(3)));
                        comments.add(createComment(
                                        ticket1,
                                        ticket1.getAssignedTo(),
                                        "SecurityConfig'te allowed origins listesini güncelledim. Lütfen tekrar deneyin.",
                                        ticket1.getCreatedAt().plusHours(5)));
                        comments.add(createComment(
                                        ticket1,
                                        ticket1.getCreatedBy(),
                                        "Harika! Şimdi çalışıyor, teşekkür ederim.",
                                        ticket1.getCreatedAt().plusHours(6)));
                }

                // Ticket 2 için yorumlar (Dashboard yüklenmiyor - CLOSED)
                Ticket ticket2 = tickets.stream()
                                .filter(t -> t.getTitle().contains("Dashboard sayfası"))
                                .findFirst().orElse(null);

                if (ticket2 != null) {
                        comments.add(createComment(
                                        ticket2,
                                        ticket2.getAssignedTo(),
                                        "Hangi tarayıcı kullanıyorsunuz? Browser console'da hata var mı?",
                                        ticket2.getCreatedAt().plusHours(2)));
                        comments.add(createComment(
                                        ticket2,
                                        ticket2.getCreatedBy(),
                                        "Chrome kullanıyorum. Console'da '404 Not Found - /api/dashboard/stats' hatası var.",
                                        ticket2.getCreatedAt().plusHours(4)));
                        comments.add(createComment(
                                        ticket2,
                                        ticket2.getAssignedTo(),
                                        "Endpoint yolunu kontrol ettim, backend'de değişiklik olmuş. Frontend'i güncelleyeceğim.",
                                        ticket2.getCreatedAt().plusHours(6)));
                        comments.add(createComment(
                                        ticket2,
                                        ticket2.getAssignedTo(),
                                        "Düzeltme yapıldı. Cache'inizi temizleyip sayfayı yenileyin (Ctrl+Shift+R).",
                                        ticket2.getCreatedAt().plusDays(1)));
                        comments.add(createComment(
                                        ticket2,
                                        ticket2.getCreatedBy(),
                                        "Mükemmel çalışıyor, sorun çözüldü!",
                                        ticket2.getCreatedAt().plusDays(1).plusHours(2)));
                }

                // Ticket 3 için yorumlar (Email bildirimleri - IN_PROGRESS)
                Ticket ticket3 = tickets.stream()
                                .filter(t -> t.getTitle().contains("Email bildirimleri"))
                                .findFirst().orElse(null);

                if (ticket3 != null) {
                        comments.add(createComment(
                                        ticket3,
                                        ticket3.getAssignedTo(),
                                        "SMTP sunucu ayarlarını kontrol ediyorum. Mail servisinde bir sorun olabilir.",
                                        ticket3.getCreatedAt().plusHours(4)));
                        comments.add(createComment(
                                        ticket3,
                                        ticket3.getCreatedBy(),
                                        "Spam klasörümü de kontrol ettim ama orada da yok.",
                                        ticket3.getCreatedAt().plusHours(6)));
                        comments.add(createComment(
                                        ticket3,
                                        ticket3.getAssignedTo(),
                                        "Test mail gönderiyorum, lütfen mail kutunuzu kontrol edin.",
                                        ticket3.getCreatedAt().plusDays(1)));
                }

                // Dark mode feature request için yorumlar
                Ticket darkModeTicket = tickets.stream()
                                .filter(t -> t.getTitle().contains("Dark mode"))
                                .findFirst().orElse(null);

                if (darkModeTicket != null) {
                        comments.add(createComment(
                                        darkModeTicket,
                                        darkModeTicket.getAssignedTo(),
                                        "Harika öneri! Dark mode'u frontend roadmap'e ekledim. Tailwind CSS kullanarak implemente edeceğiz.",
                                        darkModeTicket.getCreatedAt().plusDays(2)));
                        comments.add(createComment(
                                        darkModeTicket,
                                        users.stream().filter(u -> u.getEmail().contains("zeynep")).findFirst()
                                                        .orElse(null),
                                        "Dark mode için tasarım hazır. Theme switcher component'i üzerinde çalışıyorum.",
                                        darkModeTicket.getCreatedAt().plusDays(5)));
                        comments.add(createComment(
                                        darkModeTicket,
                                        darkModeTicket.getCreatedBy(),
                                        "Harika! Çok sabırsızlanıyorum, ne zaman yayına alınır?",
                                        darkModeTicket.getCreatedAt().plusDays(6)));
                }

                // Dosya ekleme feature için yorumlar
                Ticket fileUploadTicket = tickets.stream()
                                .filter(t -> t.getTitle().contains("dosya ekleme"))
                                .findFirst().orElse(null);

                if (fileUploadTicket != null) {
                        comments.add(createComment(
                                        fileUploadTicket,
                                        users.stream().filter(u -> u.getEmail().contains("ahmet.yilmaz")).findFirst()
                                                        .orElse(null),
                                        "Çok önemli bir özellik. Backend'de MultipartFile desteği ekleyeceğiz. S3 veya local storage kullanılabilir.",
                                        fileUploadTicket.getCreatedAt().plusDays(1)));
                        comments.add(createComment(
                                        fileUploadTicket,
                                        fileUploadTicket.getCreatedBy(),
                                        "Dosya boyutu limiti ne kadar olacak? PDF, PNG, JPG formatları yeterli.",
                                        fileUploadTicket.getCreatedAt().plusDays(2)));
                }

                // Şifre değiştirme support için yorumlar
                Ticket passwordTicket = tickets.stream()
                                .filter(t -> t.getTitle().contains("Şifremi nasıl"))
                                .findFirst().orElse(null);

                if (passwordTicket != null) {
                        comments.add(createComment(
                                        passwordTicket,
                                        passwordTicket.getAssignedTo(),
                                        "Sağ üst köşedeki profil ikonuna tıklayın -> Settings -> Security sekmesi altında bulabilirsiniz.",
                                        passwordTicket.getCreatedAt().plusMinutes(30)));
                        comments.add(createComment(
                                        passwordTicket,
                                        passwordTicket.getCreatedBy(),
                                        "Buldum, çok teşekkürler! Belki daha görünür bir yere konulabilir.",
                                        passwordTicket.getCreatedAt().plusHours(1)));
                }

                // WebSocket bağlantı sorunu için yorumlar
                Ticket wsTicket = tickets.stream()
                                .filter(t -> t.getTitle().contains("WebSocket bağlantısı"))
                                .findFirst().orElse(null);

                if (wsTicket != null) {
                        comments.add(createComment(
                                        wsTicket,
                                        wsTicket.getAssignedTo(),
                                        "WebSocket timeout ayarlarını kontrol ediyorum. Hangi ağda bu sorunu yaşıyorsunuz? (Ofis/Ev/Mobil)",
                                        wsTicket.getCreatedAt().plusHours(1)));
                        comments.add(createComment(
                                        wsTicket,
                                        wsTicket.getCreatedBy(),
                                        "Ofis network'ünde. Ev internet'imde sorun yok. Firewall ile ilgili olabilir mi?",
                                        wsTicket.getCreatedAt().plusHours(2)));
                        comments.add(createComment(
                                        wsTicket,
                                        wsTicket.getAssignedTo(),
                                        "Büyük ihtimalle proxy veya firewall engelliyor. IT ekibiyle konuşacağım.",
                                        wsTicket.getCreatedAt().plusHours(3)));
                }

                // Chatbot yanıt vermiyor için yorumlar
                Ticket chatbotTicket = tickets.stream()
                                .filter(t -> t.getTitle().contains("Chatbot yanıt"))
                                .findFirst().orElse(null);

                if (chatbotTicket != null) {
                        comments.add(createComment(
                                        chatbotTicket,
                                        chatbotTicket.getAssignedTo(),
                                        "CRITICAL olarak işaretlendi. Hemen bakıyorum. API key'i kontrol ediyorum.",
                                        chatbotTicket.getCreatedAt().plusMinutes(15)));
                        comments.add(createComment(
                                        chatbotTicket,
                                        chatbotTicket.getCreatedBy(),
                                        "Console'da 'timeout after 30000ms' hatası görüyorum.",
                                        chatbotTicket.getCreatedAt().plusMinutes(30)));
                }

                commentRepository.saveAll(comments);
                log.info("[TicketCommentSeeder] Successfully seeded {} comments", comments.size());

                // Her comment için activity log oluştur
                List<TicketActivityLog> activityLogs = new ArrayList<>();
                for (TicketComment comment : comments) {
                        activityLogs.add(createActivityLog(
                                        comment.getTicket(),
                                        comment.getUser(),
                                        "COMMENT_ADDED",
                                        String.format("Comment added: %s",
                                                        comment.getCommentText().substring(0,
                                                                        Math.min(50, comment.getCommentText().length()))
                                                                        + "..."),
                                        comment.getCreatedAt()));
                }

                activityLogRepository.saveAll(activityLogs);
                log.info("[TicketCommentSeeder] Successfully seeded {} activity logs for comments",
                                activityLogs.size());
        }

        private TicketComment createComment(Ticket ticket, User user, String text, OffsetDateTime createdAt) {
                TicketComment comment = new TicketComment();
                comment.setTicket(ticket);
                comment.setUser(user);
                comment.setCommentText(text);
                comment.setCreatedAt(createdAt);
                return comment;
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
