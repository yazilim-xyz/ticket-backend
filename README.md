# 🎫 Enterprise Ticket System

Modern, ölçeklenebilir ve gerçek zamanlı bir ticket yönetim sistemi. Spring Boot backend ile geliştirilmiş, WebSocket desteği, AI chatbot entegrasyonu ve kapsamlı bildirim sistemi içerir.

## 🚀 Özellikler

### 🔐 Kimlik Doğrulama & Yetkilendirme
- JWT tabanlı güvenli authentication
- Role-based access control (ADMIN/USER)
- Email doğrulama sistemi
- Şifre sıfırlama desteği

### 🎫 Ticket Yönetimi
- Kapsamlı ticket oluşturma ve takip sistemi
- Kategori: BUG, FEATURE, SUPPORT, OTHER
- Priority seviyeleri: CRITICAL, HIGH, MEDIUM, LOW
- Status yönetimi: OPEN, IN_PROGRESS, RESOLVED, CLOSED, CANCELLED
- Ticket atama ve yeniden atama
- Due date takibi
- Soft delete özelliği

### 💬 İletişim Özellikleri
- **Internal Chat**: Kullanıcılar arası gerçek zamanlı mesajlaşma
- **Ticket Comments**: Ticket üzerinde yorum sistemi
- **Activity Logs**: Tüm işlemlerin detaylı kaydı

### 🔔 Bildirim Sistemi
- WebSocket üzerinden gerçek zamanlı bildirimler
- Bildirim tipleri:
  - Ticket atandığında
  - Status değiştiğinde
  - Yeni yorum yapıldığında
  - Chat mesajı geldiğinde
- Okundu/okunmadı takibi
- Browser notification desteği

### 🤖 AI Chatbot
- Google Gemini AI entegrasyonu
- Ticket çözümü için akıllı öneriler
- Sistem kullanımı hakkında yardım
- Sorun giderme asistanı

### 👤 Kullanıcı Yönetimi
- Kullanıcı profil yönetimi
- Tercih sistemi (dil, tema, bildirimler)
- Kullanıcı onaylama sistemi
- Aktif/pasif kullanıcı yönetimi

### 📊 Dashboard & Raporlama
- Ticket istatistikleri
- Status dağılımı
- Priority analizi
- Kategori bazlı raporlar

## 🛠️ Teknolojiler

- **Backend Framework**: Spring Boot 3.5.6
- **Java Version**: 21
- **Database**: PostgreSQL 15
- **Security**: Spring Security + JWT
- **WebSocket**: STOMP over SockJS
- **API Documentation**: Swagger/OpenAPI
- **Validation**: Jakarta Validation
- **AI Integration**: Google Gemini API
- **Build Tool**: Maven
- **Container**: Docker & Docker Compose

## 📋 Database Schema

```dbml
Table users {
  id bigint [pk, increment]
  name varchar(50) [not null]
  surname varchar(50) [not null]
  email varchar(100) [not null, unique]
  phone_number varchar(20)
  password_hash varchar(255) [not null]
  role varchar(30) [not null, note: 'ENUM: USER, ADMIN']
  created_at timestamp [not null]
  updated_at timestamp [not null]
  is_active boolean [not null, default: true]
  is_approved boolean [not null, default: false]
}

Table tickets {
  id bigint [pk, increment]
  title varchar(255) [not null]
  description text [not null]
  status varchar(50) [not null, default: 'OPEN']
  priority varchar(50) [not null, default: 'MEDIUM']
  category varchar(50) [not null, default: 'OTHER']
  created_by_id bigint [ref: > users.id]
  assigned_to_id bigint [ref: > users.id]
  due_date timestamp
  resolution_summary text
  is_deleted boolean [not null, default: false]
  created_at timestamp [not null]
  updated_at timestamp [not null]
}

Table ticket_comments {
  id bigint [pk, increment]
  ticket_id bigint [not null, ref: > tickets.id]
  user_id bigint [ref: > users.id]
  comment_text text [not null]
  created_at timestamp
}

Table ticket_notifications {
  id bigint [pk, increment]
  ticket_id bigint [ref: > tickets.id]
  user_id bigint [not null, ref: > users.id]
  title varchar(255)
  message text [not null]
  type varchar(50) [not null, note: 'ENUM: NotificationType']
  is_read boolean [default: false]
  created_at timestamp
}

Table ticket_activity_logs {
  id bigint [pk, increment]
  ticket_id bigint [not null, ref: > tickets.id]
  user_id bigint [ref: > users.id]
  action_type varchar(50) [not null]
  action_details text
  created_at timestamp
}

Table internal_chats {
  id bigint [pk, increment]
  sender_id bigint [not null, ref: > users.id]
  receiver_id bigint [not null, ref: > users.id]
  message text [not null]
  is_read boolean [default: false]
  created_at timestamp
}

Table user_preferences {
  id bigint [pk, increment]
  user_id bigint [not null, unique, ref: - users.id]
  language varchar(10)
  theme_color varchar(20)
  notification_pref boolean
  updated_at timestamp
}
```

[DBDiagram.io](https://dbdiagram.io) üzerinde görselleştirmek için yukarıdaki kodu kopyalayın.

## 🚀 Kurulum

### Gereksinimler

- Java 21
- Docker & Docker Compose
- Maven 3.9+
- PostgreSQL 15 (Docker ile kurulacak)

### 1️⃣ Repository'yi Klonlayın

```bash
git clone https://github.com/yourusername/enterprise-ticket-system.git
cd enterprise-ticket-system
```

### 2️⃣ Environment Variables

`.env` dosyası oluşturun:

```env
# Database
POSTGRES_DB=ticket_system
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

# JWT
JWT_SECRET=your-super-secret-key-change-this-in-production
JWT_EXPIRATION=86400000

# Google Gemini AI
GEMINI_API_KEY=your-gemini-api-key

# Application
SPRING_PROFILES_ACTIVE=dev
```

### 3️⃣ Docker ile Başlatma

```bash
# PostgreSQL container'ını başlat
docker-compose up -d postgres

# Uygulamayı başlat
./mvnw spring-boot:run
```

### 4️⃣ Docker ile Tüm Sistemi Başlatma

```bash
# Tüm servisleri başlat (PostgreSQL + Backend)
docker-compose up -d

# Logları izle
docker-compose logs -f
```

## 🎯 Seed Data

Uygulama ilk çalıştırıldığında otomatik olarak test verileri eklenir:

### Kullanıcılar

**Admin Hesapları:**
- `admin@local` / `Admin123!`
- `ahmet.yilmaz@enterprise.com` / `Pass123!`
- `elif.kaya@enterprise.com` / `Pass123!`

**User Hesapları:**
- `mehmet.demir@enterprise.com` / `Pass123!`
- `zeynep.celik@enterprise.com` / `Pass123!`
- *(Toplam 13 user hesabı)*

### Test Verileri
- **23+ Ticket** (BUG, FEATURE, SUPPORT, OTHER)
- **30+ Yorum**
- **100+ Activity Log**
- **25+ Chat Mesajı**
- **Kullanıcı Tercihleri** (her kullanıcı için)

Seed data'yı devre dışı bırakmak için:

```properties
# application.properties
app.seed.enabled=false
```

## 📚 API Dokümantasyonu

Swagger UI'ya erişim:

```
http://localhost:8081/swagger-ui/index.html
```

OpenAPI JSON:

```
http://localhost:8081/v3/api-docs
```

### Temel Endpoint'ler

#### Authentication
- `POST /api/auth/register` - Yeni kullanıcı kaydı
- `POST /api/auth/login` - Giriş yap
- `POST /api/auth/refresh` - Token yenile

#### Tickets
- `GET /api/tickets` - Tüm ticket'ları listele
- `POST /api/tickets` - Yeni ticket oluştur
- `GET /api/tickets/{id}` - Ticket detayı
- `PUT /api/tickets/{id}` - Ticket güncelle
- `PATCH /api/tickets/{id}/status` - Status değiştir
- `POST /api/tickets/{id}/comments` - Yorum ekle

#### Admin
- `POST /api/admin/tickets/{id}/assign` - Ticket ata
- `GET /api/admin/users` - Kullanıcı listesi
- `PATCH /api/admin/users/{id}/approve` - Kullanıcı onayla

#### Notifications
- `GET /api/notifications` - Bildirimler
- `GET /api/notifications/unread-count` - Okunmamış sayısı
- `PATCH /api/notifications/{id}/read` - Okundu işaretle
- `PATCH /api/notifications/mark-all-read` - Tümünü okundu işaretle

#### Chat
- `GET /api/messages/{userId}` - Chat geçmişi
- `WS /ws` - WebSocket bağlantısı
  - `/app/chat.send` - Mesaj gönder
  - `/user/queue/messages` - Mesaj al

#### Chatbot
- `POST /api/chatbot/ask` - AI'ya soru sor

## 🔌 WebSocket Integration

### Frontend Bağlantısı

```javascript
import SockJS from 'sockjs-client';
import { Stomp } from 'stompjs';

const socket = new SockJS('http://localhost:8081/ws');
const stompClient = Stomp.over(socket);

stompClient.connect(
  { 'Authorization': 'Bearer ' + jwtToken },
  (frame) => {
    console.log('✅ Connected');
    
    // Bildirimleri dinle
    stompClient.subscribe('/user/queue/notifications', (message) => {
      const notification = JSON.parse(message.body);
      console.log('🔔 New notification:', notification);
    });
    
    // Chat mesajlarını dinle
    stompClient.subscribe('/user/queue/messages', (message) => {
      const chat = JSON.parse(message.body);
      console.log('💬 New message:', chat);
    });
  }
);
```

### Test Sayfaları

- **Notification Test**: `http://localhost:8081/notification-test.html`
- **Chat Test**: `http://localhost:8081/chat-test.html`

## 🧪 Testing

```bash
# Unit testleri çalıştır
./mvnw test

# Integration testleri çalıştır
./mvnw verify

# Test coverage raporu
./mvnw jacoco:report
```

## 📦 Production Build

```bash
# JAR dosyası oluştur
./mvnw clean package -DskipTests

# Docker image oluştur
docker build -t enterprise-ticket-system:latest .

# Docker ile çalıştır
docker run -p 8081:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/ticket_system \
  -e JWT_SECRET=your-production-secret \
  enterprise-ticket-system:latest
```

## 🔧 Configuration

### application.properties

```properties
# Server
server.port=8081

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/ticket_system
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# JWT
jwt.secret=${JWT_SECRET:default-secret-key}
jwt.expiration=86400000

# Gemini AI
gemini.api.key=${GEMINI_API_KEY}
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent

# Seed Data
app.seed.enabled=true
```

## 🏗️ Proje Yapısı

```
src/main/java/com/yazilimxyz/enterprise_ticket_system/
├── bootstrap/          # Seed data seeder'ları
├── configuration/      # Spring konfigürasyonları
├── controller/         # REST API endpoint'leri
│   ├── admin/         # Admin endpoint'leri
│   ├── auth/          # Authentication endpoint'leri
│   ├── chatbot/       # AI chatbot endpoint'leri
│   ├── notification/  # Bildirim endpoint'leri
│   └── ticket/        # Ticket endpoint'leri
├── dto/               # Data Transfer Objects
├── entities/          # JPA Entity'ler
├── enums/             # Enum tanımları
├── exception/         # Custom exception'lar
├── repository/        # JPA Repository'ler
├── security/          # Security konfigürasyonu
├── service/           # Business logic
│   ├── admin/        # Admin servisleri
│   ├── auth/         # Auth servisleri
│   ├── chatbot/      # AI chatbot servisi
│   ├── notification/ # Bildirim servisi
│   └── ticket/       # Ticket servisleri
└── websocket/         # WebSocket konfigürasyonu
```

## 🤝 Katkıda Bulunma

1. Fork edin
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Commit edin (`git commit -m 'feat: Add amazing feature'`)
4. Push edin (`git push origin feature/amazing-feature`)
5. Pull Request açın

## 📝 Commit Konvansiyonları

```
feat: Yeni özellik
fix: Bug düzeltmesi
docs: Dokümantasyon değişikliği
style: Kod formatı (loglama, boşluk vb.)
refactor: Kod refactoring
test: Test ekleme/düzeltme
chore: Build/config değişiklikleri
```

## 🐛 Sorun Giderme

### PostgreSQL Bağlantı Hatası

```bash
# Container'ı yeniden başlat
docker-compose restart postgres

# Logları kontrol et
docker-compose logs postgres
```

### WebSocket Bağlanamıyor

- CORS ayarlarını kontrol edin
- JWT token'ın geçerli olduğundan emin olun
- Browser console'da hata mesajlarını kontrol edin

### Seed Data Yüklenmiyor

```bash
# Veritabanını sıfırla
docker-compose down -v
docker-compose up -d postgres

# Uygulamayı yeniden başlat
./mvnw spring-boot:run
```

## 📄 Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

## 👨‍💻 Geliştirici

**Yazilim XYZ**
- Website: [yazilimxyz.com](https://yazilimxyz.com)
- Email: support@yazilimxyz.com

## 🙏 Teşekkürler

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Google Gemini AI](https://ai.google.dev/)
- [PostgreSQL](https://www.postgresql.org/)
- [SockJS](https://github.com/sockjs)
- [STOMP](https://stomp.github.io/)