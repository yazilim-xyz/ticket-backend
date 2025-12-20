# 🔔 Notification System Integration Guide

Bu dokümantasyon, React uygulamanızda WebSocket tabanlı bildirim sistemini nasıl entegre edeceğinizi açıklar.

---

## 📋 İçindekler

1. [Gerekli Paketler](#gerekli-paketler)
2. [WebSocket Bağlantısı](#websocket-bağlantısı)
3. [Bildirim Tipleri](#bildirim-tipleri)
4. [Bildirim Alma](#bildirim-alma)
5. [Bildirim Geçmişi](#bildirim-geçmişi)
6. [Bildirim Durumu Güncelleme](#bildirim-durumu-güncelleme)
7. [Örnek React Hook](#örnek-react-hook)
8. [Hangi Durumlarda Bildirim Gönderilir](#hangi-durumlarda-bildirim-gönderilir)

---

## 🔧 Gerekli Paketler

```bash
npm install sockjs-client stompjs
```

veya

```bash
yarn add sockjs-client stompjs
```

---

## 🔌 WebSocket Bağlantısı

### Endpoint
```
ws://localhost:8081/ws
```

### Bağlantı Kurma

```javascript
import SockJS from 'sockjs-client';
import { Stomp } from 'stompjs';

const connectToNotifications = (jwtToken) => {
  const socket = new SockJS('http://localhost:8081/ws');
  const stompClient = Stomp.over(socket);

  stompClient.connect(
    { 'Authorization': 'Bearer ' + jwtToken },
    (frame) => {
      console.log('✅ Notification WebSocket bağlantısı başarılı');
      
      // Bildirimleri dinlemeye başla
      stompClient.subscribe('/user/queue/notifications', (message) => {
        const notification = JSON.parse(message.body);
        console.log('🔔 Yeni bildirim:', notification);
        
        // Bildirim göster (browser notification veya toast)
        showNotification(notification);
      });
    },
    (error) => {
      console.error('❌ WebSocket bağlantı hatası:', error);
    }
  );

  return stompClient;
};
```

### JWT Token'dan User ID Çıkarma

```javascript
const getUserIdFromToken = (jwtToken) => {
  try {
    const payload = JSON.parse(atob(jwtToken.split('.')[1]));
    return payload.sub; // User ID
  } catch (error) {
    console.error('JWT parse hatası:', error);
    return null;
  }
};
```

---

## 📋 Bildirim Tipleri

Sistem şu bildirim tiplerini desteklemektedir:

```typescript
enum NotificationType {
  TICKET_ASSIGNED = 'TICKET_ASSIGNED',           // Ticket size atandı
  TICKET_STATUS_CHANGED = 'TICKET_STATUS_CHANGED', // Ticket durumu değişti
  NEW_COMMENT = 'NEW_COMMENT',                   // Yeni yorum yapıldı
  NEW_MESSAGE = 'NEW_MESSAGE',                   // Yeni chat mesajı (opsiyonel)
  TICKET_DUE_SOON = 'TICKET_DUE_SOON',          // Ticket süresi dolmak üzere
  SYSTEM_ANNOUNCEMENT = 'SYSTEM_ANNOUNCEMENT'    // Sistem duyurusu
}
```

---

## 📥 Bildirim Alma

WebSocket üzerinden gelen bildirimlar **DTO formatında** gelir:

```typescript
interface NotificationDto {
  id: number;
  userId: number;
  title: string;
  message: string;
  type: NotificationType;
  isRead: boolean;
  createdAt: string; // ISO 8601 format
}
```

### Subscribe Etme

```javascript
stompClient.subscribe('/user/queue/notifications', (message) => {
  const notification = JSON.parse(message.body);
  
  // notification artık DTO formatında
  console.log(`📢 ${notification.title}: ${notification.message}`);
  
  // Bildirimi state'e ekle veya UI'da göster
  handleNewNotification(notification);
  
  // Browser notification göster (opsiyonel)
  if (Notification.permission === 'granted') {
    new Notification(notification.title, {
      body: notification.message,
      icon: '/notification-icon.png'
    });
  }
});
```

---

## 📜 Bildirim Geçmişi

### REST API Endpoint
```
GET /api/notifications
```

### Headers
```
Authorization: Bearer {jwtToken}
```

### Response Formatı

```typescript
interface NotificationDto {
  id: number;
  userId: number;
  title: string;
  message: string;
  type: NotificationType;
  isRead: boolean;
  createdAt: string; // ISO 8601 format
}
```

### Örnek Kullanım

```javascript
const loadNotifications = async (jwtToken) => {
  try {
    const response = await fetch(
      'http://localhost:8081/api/notifications',
      {
        headers: {
          'Authorization': `Bearer ${jwtToken}`
        }
      }
    );
    
    const notifications = await response.json();
    return notifications;
  } catch (error) {
    console.error('Bildirimler yüklenirken hata:', error);
    return [];
  }
};
```

---

## 🔢 Okunmamış Bildirim Sayısı

### REST API Endpoint
```
GET /api/notifications/unread-count
```

### Headers
```
Authorization: Bearer {jwtToken}
```

### Response Formatı

```json
{
  "count": 5
}
```

### Örnek Kullanım

```javascript
const getUnreadCount = async (jwtToken) => {
  try {
    const response = await fetch(
      'http://localhost:8081/api/notifications/unread-count',
      {
        headers: {
          'Authorization': `Bearer ${jwtToken}`
        }
      }
    );
    
    const data = await response.json();
    return data.count;
  } catch (error) {
    console.error('Okunmamış bildirim sayısı alınırken hata:', error);
    return 0;
  }
};
```

---

## ✅ Bildirim Durumu Güncelleme

### Tek Bildirimi Okundu Olarak İşaretle

#### REST API Endpoint
```
PATCH /api/notifications/{id}/read
```

#### Headers
```
Authorization: Bearer {jwtToken}
```

#### Örnek Kullanım

```javascript
const markAsRead = async (notificationId, jwtToken) => {
  try {
    await fetch(
      `http://localhost:8081/api/notifications/${notificationId}/read`,
      {
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${jwtToken}`
        }
      }
    );
    console.log('✅ Bildirim okundu olarak işaretlendi');
  } catch (error) {
    console.error('Bildirim güncellenirken hata:', error);
  }
};
```

### Tüm Bildirimleri Okundu Olarak İşaretle

#### REST API Endpoint
```
PATCH /api/notifications/mark-all-read
```

#### Headers
```
Authorization: Bearer {jwtToken}
```

#### Örnek Kullanım

```javascript
const markAllAsRead = async (jwtToken) => {
  try {
    await fetch(
      'http://localhost:8081/api/notifications/mark-all-read',
      {
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${jwtToken}`
        }
      }
    );
    console.log('✅ Tüm bildirimler okundu olarak işaretlendi');
  } catch (error) {
    console.error('Bildirimler güncellenirken hata:', error);
  }
};
```

---

## 🎣 Örnek React Hook

### `useNotifications.js`

```javascript
import { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from 'stompjs';

const useNotifications = (jwtToken) => {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [connected, setConnected] = useState(false);
  const stompClient = useRef(null);
  const currentUserId = useRef(null);

  useEffect(() => {
    if (!jwtToken) return;

    // JWT'den user ID'yi al
    try {
      const payload = JSON.parse(atob(jwtToken.split('.')[1]));
      currentUserId.current = payload.sub;
    } catch (error) {
      console.error('JWT parse hatası:', error);
      return;
    }

    // WebSocket bağlantısı kur
    const socket = new SockJS('http://localhost:8081/ws');
    stompClient.current = Stomp.over(socket);

    stompClient.current.connect(
      { 'Authorization': 'Bearer ' + jwtToken },
      () => {
        console.log('✅ Notification bağlantısı kuruldu');
        setConnected(true);

        // Bildirimleri dinle
        stompClient.current.subscribe('/user/queue/notifications', (message) => {
          const notification = JSON.parse(message.body);
          
          // Yeni bildirimi listeye ekle
          setNotifications((prev) => [notification, ...prev]);
          
          // Okunmamış sayısını artır
          setUnreadCount((prev) => prev + 1);
          
          // Browser notification göster
          if (Notification.permission === 'granted') {
            new Notification(notification.title, {
              body: notification.message,
              icon: '/notification-icon.png'
            });
          }
        });

        // İlk yüklemede bildirimleri getir
        loadNotifications();
        loadUnreadCount();
      },
      (error) => {
        console.error('❌ Bağlantı hatası:', error);
        setConnected(false);
      }
    );

    // Browser notification izni iste
    if (Notification.permission === 'default') {
      Notification.requestPermission();
    }

    // Cleanup
    return () => {
      if (stompClient.current) {
        stompClient.current.disconnect();
      }
    };
  }, [jwtToken]);

  // Bildirim geçmişini yükle
  const loadNotifications = async () => {
    try {
      const response = await fetch(
        'http://localhost:8081/api/notifications',
        {
          headers: {
            'Authorization': `Bearer ${jwtToken}`
          }
        }
      );
      const data = await response.json();
      setNotifications(data);
    } catch (error) {
      console.error('Bildirimler yüklenirken hata:', error);
    }
  };

  // Okunmamış sayısını yükle
  const loadUnreadCount = async () => {
    try {
      const response = await fetch(
        'http://localhost:8081/api/notifications/unread-count',
        {
          headers: {
            'Authorization': `Bearer ${jwtToken}`
          }
        }
      );
      const data = await response.json();
      setUnreadCount(data.count);
    } catch (error) {
      console.error('Okunmamış sayısı alınırken hata:', error);
    }
  };

  // Tek bildirimi okundu işaretle
  const markAsRead = async (notificationId) => {
    try {
      await fetch(
        `http://localhost:8081/api/notifications/${notificationId}/read`,
        {
          method: 'PATCH',
          headers: {
            'Authorization': `Bearer ${jwtToken}`
          }
        }
      );
      
      // Local state'i güncelle
      setNotifications((prev) =>
        prev.map((n) =>
          n.id === notificationId ? { ...n, isRead: true } : n
        )
      );
      
      // Okunmamış sayısını azalt
      setUnreadCount((prev) => Math.max(0, prev - 1));
    } catch (error) {
      console.error('Bildirim güncellenirken hata:', error);
    }
  };

  // Tüm bildirimleri okundu işaretle
  const markAllAsRead = async () => {
    try {
      await fetch(
        'http://localhost:8081/api/notifications/mark-all-read',
        {
          method: 'PATCH',
          headers: {
            'Authorization': `Bearer ${jwtToken}`
          }
        }
      );
      
      // Local state'i güncelle
      setNotifications((prev) =>
        prev.map((n) => ({ ...n, isRead: true }))
      );
      
      setUnreadCount(0);
    } catch (error) {
      console.error('Bildirimler güncellenirken hata:', error);
    }
  };

  return {
    notifications,
    unreadCount,
    connected,
    currentUserId: currentUserId.current,
    markAsRead,
    markAllAsRead,
    loadNotifications,
    loadUnreadCount
  };
};

export default useNotifications;
```

### Kullanım Örneği

```javascript
import React from 'react';
import useNotifications from './hooks/useNotifications';

const NotificationComponent = ({ jwtToken }) => {
  const {
    notifications,
    unreadCount,
    connected,
    markAsRead,
    markAllAsRead
  } = useNotifications(jwtToken);

  // Bildirim tipine göre ikon
  const getNotificationIcon = (type) => {
    switch (type) {
      case 'TICKET_ASSIGNED':
        return '📌';
      case 'TICKET_STATUS_CHANGED':
        return '🔄';
      case 'NEW_COMMENT':
        return '💬';
      case 'NEW_MESSAGE':
        return '📨';
      case 'TICKET_DUE_SOON':
        return '⏰';
      case 'SYSTEM_ANNOUNCEMENT':
        return '📢';
      default:
        return '🔔';
    }
  };

  return (
    <div className="notification-container">
      <div className="notification-header">
        <h2>Bildirimler</h2>
        <div>
          <span className="badge">
            {unreadCount} Okunmamış
          </span>
          <span className={connected ? 'status-online' : 'status-offline'}>
            {connected ? '🟢 Bağlı' : '🔴 Bağlantı Kesildi'}
          </span>
        </div>
        {unreadCount > 0 && (
          <button onClick={markAllAsRead}>
            Tümünü Okundu İşaretle
          </button>
        )}
      </div>

      <div className="notification-list">
        {notifications.length === 0 ? (
          <p className="no-notifications">Henüz bildirim yok</p>
        ) : (
          notifications.map((notification) => (
            <div
              key={notification.id}
              className={`notification-item ${!notification.isRead ? 'unread' : ''}`}
              onClick={() => !notification.isRead && markAsRead(notification.id)}
            >
              <div className="notification-icon">
                {getNotificationIcon(notification.type)}
              </div>
              <div className="notification-content">
                <h4>{notification.title}</h4>
                <p>{notification.message}</p>
                <small>
                  {new Date(notification.createdAt).toLocaleString('tr-TR')}
                </small>
              </div>
              {!notification.isRead && (
                <div className="unread-indicator">●</div>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default NotificationComponent;
```

### CSS Örneği

```css
.notification-container {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #eee;
}

.badge {
  background-color: #ff4444;
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  margin-right: 10px;
}

.status-online, .status-offline {
  font-size: 12px;
  font-weight: bold;
}

.notification-list {
  max-height: 600px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 15px;
  margin-bottom: 10px;
  border-radius: 8px;
  background-color: #f9f9f9;
  cursor: pointer;
  transition: background-color 0.2s;
}

.notification-item:hover {
  background-color: #f0f0f0;
}

.notification-item.unread {
  background-color: #e3f2fd;
  border-left: 4px solid #2196f3;
}

.notification-icon {
  font-size: 24px;
  margin-right: 15px;
}

.notification-content {
  flex: 1;
}

.notification-content h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
}

.notification-content p {
  margin: 0 0 5px 0;
  color: #666;
}

.notification-content small {
  color: #999;
  font-size: 12px;
}

.unread-indicator {
  color: #2196f3;
  font-size: 20px;
  margin-left: 10px;
}

.no-notifications {
  text-align: center;
  color: #999;
  padding: 40px;
}
```

---

## 🔔 Hangi Durumlarda Bildirim Gönderilir?

Sistem aşağıdaki durumlarda otomatik olarak bildirim gönderir:

### 1. **Ticket Atama** (`TICKET_ASSIGNED`)
- **Ne zaman:** Bir ticket bir kullanıcıya atandığında
- **Kime:** Ticket'ın atandığı kullanıcıya
- **Mesaj:** "Ticket #{ticketId} size atandı: {ticketTitle}"
- **Tetikleyen İşlem:** `assignTicket()` metodu

```java
// TicketService.java
notificationService.createAndSendNotification(
    assignedTo.getId(),
    "Yeni Ticket Atandı",
    String.format("Ticket #%d size atandı: %s", ticket.getId(), ticket.getTitle()),
    NotificationType.TICKET_ASSIGNED,
    ticket.getId()
);
```

### 2. **Ticket Durum Değişikliği** (`TICKET_STATUS_CHANGED`)
- **Ne zaman:** Bir ticket'ın durumu değiştirildiğinde (OPEN, IN_PROGRESS, RESOLVED, CLOSED, vb.)
- **Kime:** 
  - Ticket'ı oluşturan kullanıcıya
  - Ticket'ın atandığı kullanıcıya (eğer farklı ise)
- **Mesaj:** "Ticket #{ticketId} durumu '{yeniDurum}' olarak değiştirildi"
- **Tetikleyen İşlem:** `updateStatus()` metodu

```java
// TicketService.java
// Ticket sahibine bildirim
notificationService.createAndSendNotification(
    ticket.getCreatedBy().getId(),
    "Ticket Durumu Değişti",
    String.format("Ticket #%d durumu '%s' olarak değiştirildi", 
        ticket.getId(), request.getStatus()),
    NotificationType.TICKET_STATUS_CHANGED,
    ticket.getId()
);

// Atanan kişiye de bildirim (eğer farklı ise)
if (ticket.getAssignedTo() != null && 
    !ticket.getAssignedTo().getId().equals(ticket.getCreatedBy().getId())) {
    notificationService.createAndSendNotification(
        ticket.getAssignedTo().getId(),
        "Ticket Durumu Değişti",
        String.format("Ticket #%d durumu '%s' olarak değiştirildi", 
            ticket.getId(), request.getStatus()),
        NotificationType.TICKET_STATUS_CHANGED,
        ticket.getId()
    );
}
```

### 3. **Yeni Yorum** (`NEW_COMMENT`)
- **Ne zaman:** Bir ticket'a yeni yorum eklendiğinde
- **Kime:** 
  - Ticket'ı oluşturan kullanıcıya (yorum yapan kendisi değilse)
  - Ticket'ın atandığı kullanıcıya (yorum yapan kendisi değilse ve ticket sahibinden farklı ise)
- **Mesaj:** "{kullanıcıAdı} ticket #{ticketId}'e yorum yaptı"
- **Tetikleyen İşlem:** `addComment()` metodu

```java
// TicketService.java
// Ticket sahibine bildirim (eğer yorum yapan kendisi değilse)
if (ticket.getCreatedBy() != null && 
    !ticket.getCreatedBy().getId().equals(author.getId())) {
    notificationService.createAndSendNotification(
        ticket.getCreatedBy().getId(),
        "Yeni Yorum",
        String.format("%s ticket #%d'e yorum yaptı", 
            author.getFullName(), ticketId),
        NotificationType.NEW_COMMENT,
        ticketId
    );
}

// Atanan kişiye de bildirim (eğer farklı ise)
if (ticket.getAssignedTo() != null &&
    !ticket.getAssignedTo().getId().equals(author.getId()) &&
    !ticket.getAssignedTo().getId().equals(ticket.getCreatedBy().getId())) {
    notificationService.createAndSendNotification(
        ticket.getAssignedTo().getId(),
        "Yeni Yorum",
        String.format("%s ticket #%d'e yorum yaptı", 
            author.getFullName(), ticketId),
        NotificationType.NEW_COMMENT,
        ticketId
    );
}
```

### 4. **Diğer Bildirim Tipleri** (Gelecekte Eklenebilir)

#### `NEW_MESSAGE`
- Chat sistemi ile entegre edildiğinde kullanılabilir
- Yeni bir chat mesajı geldiğinde bildirim gönderir

#### `TICKET_DUE_SOON`
- Scheduled job ile ticket'ların bitiş tarihini kontrol eder
- Bitiş tarihi yaklaşan ticket'lar için bildirim gönderir

#### `SYSTEM_ANNOUNCEMENT`
- Admin tarafından yapılan sistem geneli duyurular için
- Tüm kullanıcılara veya belirli gruplara bildirim gönderilebilir

---

## ⚠️ Önemli Notlar

1. **JWT Token**: Her WebSocket bağlantısında ve API isteğinde `Authorization` header'ı gönderilmelidir.

2. **User ID**: JWT token'ın `sub` claim'inde user ID bulunur.

3. **Bildirim Formatı**: Hem WebSocket hem REST API **aynı DTO formatını** kullanır (`NotificationDto`). Bu tutarlılık frontend kodunu basitleştirir.

4. **Bağlantı Yönetimi**: Component unmount olduğunda WebSocket bağlantısını kapatmayı unutmayın.

5. **Browser Notification**: Modern tarayıcılarda browser notification desteği için kullanıcıdan izin almanız gerekir.

6. **Gerçek Zamanlı Güncelleme**: WebSocket üzerinden gelen yeni bildirimler otomatik olarak UI'da gösterilir ve okunmamış sayısı güncellenir.

7. **Error Handling**: Bağlantı kopması durumunda kullanıcıyı bilgilendirin ve yeniden bağlanma mekanizması ekleyin.

8. **CORS**: Production'da backend CORS ayarlarını düzenleyin.

---

## 🔐 Güvenlik

- JWT token'ları güvenli bir şekilde saklayın (localStorage veya httpOnly cookies)
- WebSocket bağlantısı her zaman JWT ile korunur
- User ID doğrulaması backend tarafında yapılır
- Kullanıcılar sadece kendi bildirimlerini görebilir ve güncelleyebilir

---

## 🐛 Sorun Giderme

### Bağlantı Kurulamıyor
- Backend'in çalıştığından emin olun
- JWT token'ın geçerli olduğunu kontrol edin
- CORS ayarlarını kontrol edin
- WebSocket endpoint'inin doğru olduğunu kontrol edin (`/ws`)

### Bildirimler Gelmiyor
- `/user/queue/notifications` endpoint'ine subscribe edildiğinden emin olun
- WebSocket bağlantısının aktif olduğunu kontrol edin
- Browser console'da hata mesajlarını kontrol edin

### Bildirim Geçmişi Yüklenmiyor
- API endpoint'inin doğru olduğunu kontrol edin (`/api/notifications`)
- Authorization header'ının gönderildiğinden emin olun
- Network tab'inde HTTP status code'u kontrol edin

### Browser Notification Çalışmıyor
- Tarayıcıdan notification izni istendiğinden emin olun
- `Notification.permission` değerini kontrol edin
- HTTPS kullanıyor olmanız gerekebilir (production'da)

---

## 📊 API Özeti

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/api/notifications` | Kullanıcının tüm bildirimlerini getir |
| GET | `/api/notifications/unread-count` | Okunmamış bildirim sayısını getir |
| PATCH | `/api/notifications/{id}/read` | Tek bildirimi okundu işaretle |
| PATCH | `/api/notifications/mark-all-read` | Tüm bildirimleri okundu işaretle |
| WS | `/user/queue/notifications` | Gerçek zamanlı bildirim alma |

---

## 🎨 UI/UX Önerileri

1. **Bildirim Badge**: Navbar'da okunmamış bildirim sayısını gösterin
2. **Ses Bildirimi**: Yeni bildirim geldiğinde ses çalabilirsiniz
3. **Animasyon**: Yeni bildirimler için fade-in animasyonu ekleyin
4. **Gruplama**: Aynı tipteki bildirimleri gruplayın
5. **Filtreleme**: Bildirim tipine göre filtreleme özelliği ekleyin
6. **Arama**: Bildirim geçmişinde arama yapma imkanı sunun
7. **Silme**: Kullanıcıların eski bildirimleri silebilmesini sağlayın (gelecek özellik)

---

İyi kodlamalar! 🚀
