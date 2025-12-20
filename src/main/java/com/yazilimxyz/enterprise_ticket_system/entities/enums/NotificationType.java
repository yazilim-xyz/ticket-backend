package com.yazilimxyz.enterprise_ticket_system.entities.enums;

public enum NotificationType {
    TICKET_ASSIGNED, // Ticket size atandı
    TICKET_STATUS_CHANGED, // Ticket durumu değişti
    NEW_COMMENT, // Yeni yorum yapıldı
    NEW_MESSAGE, // Yeni chat mesajı (opsiyonel)
    TICKET_DUE_SOON, // Ticket süresi dolmak üzere
    SYSTEM_ANNOUNCEMENT // Sistem duyurusu
}