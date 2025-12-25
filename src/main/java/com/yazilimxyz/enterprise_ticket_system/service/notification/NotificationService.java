package com.yazilimxyz.enterprise_ticket_system.service.notification;

import com.yazilimxyz.enterprise_ticket_system.dto.notification.NotificationDto;
import com.yazilimxyz.enterprise_ticket_system.entities.TicketNotification;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.NotificationType;
import com.yazilimxyz.enterprise_ticket_system.repository.TicketNotificationRepository;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

        private final SimpMessagingTemplate messagingTemplate;
        private final TicketNotificationRepository notificationRepository;
        private final UserRepository userRepository;

        /**
         * Kullanıcıya anlık bildirim gönder (WebSocket)
         */
        public void sendNotification(Long userId, NotificationDto notification) {
                messagingTemplate.convertAndSendToUser(
                                userId.toString(),
                                "/queue/notifications",
                                notification);
        }

        /**
         * Bildirim oluştur ve hem kaydet hem de WebSocket ile gönder
         */
        @Transactional
        public NotificationDto createAndSendNotification(
                        Long userId,
                        String title,
                        String message,
                        NotificationType type,
                        Long relatedEntityId) {

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

                // Database'e kaydet
                TicketNotification notification = new TicketNotification();
                notification.setUser(user);
                notification.setTitle(title);
                notification.setMessage(message);
                notification.setType(type);
                notification.setIsRead(false);
                notification.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

                TicketNotification saved = notificationRepository.save(notification);

                // DTO'ya dönüştür
                NotificationDto dto = new NotificationDto(
                                saved.getId(),
                                userId,
                                title,
                                message,
                                type,
                                false,
                                saved.getCreatedAt());

                // WebSocket ile gönder
                sendNotification(userId, dto);

                return dto;
        }

        /**
         * Kullanıcının tüm bildirimlerini getir
         */
        @Transactional(readOnly = true)
        public List<NotificationDto> getUserNotifications(Long userId) {
                List<TicketNotification> notifications = notificationRepository
                                .findByUserIdOrderByCreatedAtDesc(userId);

                return notifications.stream()
                                .map(n -> new NotificationDto(
                                                n.getId(),
                                                n.getUser().getId(),
                                                n.getTitle(),
                                                n.getMessage(),
                                                n.getType(),
                                                n.getIsRead(),
                                                n.getCreatedAt()))
                                .collect(Collectors.toList());
        }

        /**
         * Okunmamış bildirim sayısını getir
         */
        @Transactional(readOnly = true)
        public long getUnreadCount(Long userId) {
                return notificationRepository.countByUserIdAndIsReadFalse(userId);
        }

        /**
         * Bildirimi okundu olarak işaretle
         */
        @Transactional
        public void markAsRead(Long notificationId) {
                TicketNotification notification = notificationRepository.findById(notificationId)
                                .orElseThrow(() -> new RuntimeException("Notification not found"));

                notification.setIsRead(true);
                notificationRepository.save(notification);
        }

        /**
         * Tüm bildirimleri okundu olarak işaretle
         */
        @Transactional
        public void markAllAsRead(Long userId) {
                List<TicketNotification> notifications = notificationRepository
                                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
                notifications.forEach(n -> n.setIsRead(true));
                notificationRepository.saveAll(notifications);
        }
}
