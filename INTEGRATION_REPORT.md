# Entegrasyon Raporu

## 1) Root Proje
- Kök artık `main_branch_entegre_edilecek` içeriğiyle çalışan ana uygulama (Spring Boot 3.5.6, Java 21).
- Eski admin kaynakları tam yedek olarak `docs/legacy_admin_panel_source/` altına taşındı.
- `main_branch_entegre_edilecek/` klasörü kaldırıldı.

## 2) Admin Entegrasyonu
- Admin modülü `src/main/java/com/yazilimxyz/enterprise_ticket_system/admin` altına taşındı (controller, service, dto, mapper, exception).
- Admin DTO’lar mevcut `User`/`Ticket` entity’lerine uyarlandı (`Role` enum, `active` flag, `createdAt/updatedAt` alanları).
- Servislerde doğrulamalar eklendi: e-posta benzersizliği, şifre zorunluluğu, rol/aktif durum kontrolü, atama isteği alan doğrulamaları.
- Ticket atamalarında `assignedUser` ve `assignedByAdmin` dolduruluyor; zaman damgaları güncelleniyor.
- Admin endpointleri: `/api/admin/users/**`, `/api/admin/tickets/**` (ROLE_ADMIN).

## 3) Security / JWT / WebSocket Birleşimi
- Tekil `SecurityConfig`: `/auth/**` serbest (logout hariç authenticated), `/api/admin/**` → `hasRole('ADMIN')`, diğer tüm endpointler kimlik doğrulamalı; 401 için `HttpStatusEntryPoint`.
- JWT filtre revize: token claim’leri doğrulanıyor, DB’deki kullanıcı/rol ile tutarlılık kontrolü, `AuthenticatedUser` principal kullanımı, aktif olmayan kullanıcı reddediliyor.
- WebSocket handshake (`/ws`): Authorization Bearer token parse ediliyor, DB’de aktif kullanıcı zorunlu, principal olarak `AuthenticatedUser` atanıyor; `convertAndSendToUser` kullanıcı bazlı teslimat korunuyor.
- Logout ve chat akışı aynı principal modelini kullanıyor; kırılgan `Principal.getName()` parse işlemi kaldırıldı.

## 4) Build / Test / Run
- Çalıştırılan komutlar (kök):
  - `./mvnw -q test`
  - `./mvnw -q package`
  - `./mvnw -q spring-boot:run` (H2 varsayılanıyla ayağa kalktı)
- Varsayılan DB H2 (PostgreSQL mod). Üretim/PostgreSQL için ortam değişkenleri:
  - `DB_URL` (örn. `jdbc:postgresql://host:port/db`)
  - `DB_USERNAME`, `DB_PASSWORD`, `DB_DRIVER=org.postgresql.Driver`, `JPA_DIALECT=org.hibernate.dialect.PostgreSQLDialect`
- JWT gizli anahtarı: `JWT_SECRET` sağlam bir değerle verilmelidir (varsayılan yalnızca geliştirme içindir).

## 5) Testler
- `src/test/java/com/yazilimxyz/enterprise_ticket_system/admin/AdminEndpointSecurityTests.java`
  - /api/admin/** için 401 (anonim) ve 403 (ROLE_USER) doğrulaması
  - ROLE_ADMIN için 200 erişim doğrulaması
- `EnterpriseTicketSystemApplicationTests` context yükleniyor (H2 üzerinde).

## 6) Kalan Notlar / Riskler
- CORS izinlerini `cors.allowed-origins` (application.properties) ile ihtiyaçlarınıza göre güncelleyin.
- H2 modunda Hibernate PostgreSQLDialect uyarısı bilgi amaçlı; gerçek Postgres’e geçince ayarlarınızı (dialect/driver/url) sağlayın.
- Devtools açık; prod’da devtools devre dışı bırakılabilir.
