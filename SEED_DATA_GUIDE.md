# Seed Data Kullanım Kılavuzu

Bu proje, veritabanına otomatik olarak örnek veriler (seed data) ekleyen bir sistem içerir. Bu sayede development ve test aşamalarında gerçekçi verilerle çalışabilirsiniz.

## 📦 Eklenen Seed Data

### 1. **Kullanıcılar (UserSeeder)**
- **Admin Kullanıcılar**: 3 admin hesabı (ticket oluşturur, atar)
- **User Çalışanlar**: 12 user hesabı (ticket çözer, günlük görevleri yapar)
- **Toplam**: 15 kullanıcı + 1 default admin (AdminSeeder'dan)

**Tüm kullanıcıların şifresi**: `Pass123!`

**Örnek Hesaplar**:
- Admin: `admin@local` / `Admin123!`
- Admin: `ahmet.yilmaz@enterprise.com` / `Pass123!`
- User: `mehmet.demir@enterprise.com` / `Pass123!`
- User: `zeynep.celik@enterprise.com` / `Pass123!`

### 2. **Ticket'lar (TicketSeeder)**
- **Oluşturan**: ADMIN kullanıcıları
- **Atanan**: USER kullanıcıları (çalışanlar)
- **BUG**: 8 adet bug raporu
- **FEATURE**: 5 adet özellik talebi
- **SUPPORT**: 5 adet destek talebi
- **OTHER**: 5 adet diğer kategoride ticket
- **Toplam**: ~23 ticket

**Sistem Akışı**:
- ADMIN: Ticket oluşturur
- ADMIN: USER'a ticket atar
- USER: Kendisine atanan ticket'ları çözer

**Ticket Status Dağılımı**:
- OPEN: Yeni açılmış, henüz üzerinde çalışılmayan
- IN_PROGRESS: Üzerinde aktif çalışılan
- RESOLVED: Çözülmüş, kullanıcı onayı bekleyen
- CLOSED: Tamamen kapatılmış
- CANCELLED: İptal edilmiş

**Priority Dağılımı**:
- CRITICAL: Acil, anında müdahale gereken
- HIGH: Yüksek öncelikli
- MEDIUM: Orta öncelikli
- LOW: Düşük öncelikli

### 3. **Yorumlar (TicketCommentSeeder)**
- Ticket'lar arasında gerçekçi konuşmalar
- Support team ve kullanıcı etkileşimleri
- Sorun çözüm süreçleri
- **Toplam**: ~30+ yorum

### 4. **Activity Logs (ActivityLogSeeder)**
- Ticket oluşturma kayıtları
- Status değişiklikleri
- Atama işlemleri
- Priority güncellemeleri
- **Toplam**: 100+ log kaydı

### 5. **Kullanıcı Tercihleri (UserPreferenceSeeder)**
- Dil tercihleri (Türkçe/İngilizce)
- Tema renkleri (Light/Dark)
- Bildirim ayarları
- Her kullanıcı için otomatik oluşturulur

### 6. **Internal Chat (InternalChatSeeder)**
- Admin'ler arası koordinasyon
- Admin ve user arası iletişim  
- User'lar arası teknik tartışma
- **Toplam**: ~25 mesaj

## 🎯 Rol Sistemi

### ADMIN Rolü
- ✅ Ticket oluşturur
- ✅ User'lara ticket atar
- ✅ Proje yönetimi yapar
- ✅ Önceliklendirme kararları alır

### USER Rolü
- ✅ Kendine atanan ticket'ları görür
- ✅ Ticket'lar üzerinde çalışır
- ✅ Günlük görevlerini takip eder
- ✅ Çözümlerini dokümante eder

## 🚀 Kullanım

### Seed Data'yı Etkinleştirme

`application.properties` dosyasında:

```properties
# Tüm seed data'yı etkinleştir/devre dışı bırak
app.seed.enabled=true

# Sadece admin seeder'ı kontrol et
app.seed.admin.enabled=true
```

### Seed Data'yı Devre Dışı Bırakma

```properties
app.seed.enabled=false
```

### Production'da Seed Data'yı Kapatma

Environment variable kullanarak:

```bash
export APP_SEED_ENABLED=false
java -jar application.jar
```

Veya komut satırından:

```bash
java -jar application.jar --app.seed.enabled=false
```

## 🔄 Seed Data Çalışma Sırası

Seed data'lar belirli bir sırayla çalışır (`@Order` annotation'ı ile):

1. **Order 0**: AdminSeeder - Default admin kullanıcısı
2. **Order 1**: UserSeeder - Diğer kullanıcılar
3. **Order 2**: TicketSeeder - Ticket'lar
4. **Order 3**: TicketCommentSeeder - Yorumlar
5. **Order 4**: ActivityLogSeeder - Aktivite logları
6. **Order 5**: UserPreferenceSeeder - Kullanıcı tercihleri
7. **Order 6**: InternalChatSeeder - Chat mesajları

## 🔍 Veritabanını Sıfırlama

Eğer seed data'yı yeniden çalıştırmak isterseniz:

### PostgreSQL için:
```sql
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
```

Veya tüm tabloları silin:
```sql
TRUNCATE users, tickets, ticket_comments, ticket_activity_logs, 
         user_preferences, internal_chats, ticket_notifications 
         CASCADE;
```

### Uygulamayı Yeniden Başlatın:
```bash
./mvnw spring-boot:run
```

Veya Docker kullanıyorsanız:
```bash
docker-compose down -v
docker-compose up -d
```

## 📊 Seed Data Özellikleri

### Gerçekçi Veriler
- Türkçe içerik
- Gerçek dünya senaryoları
- Tutarlı zaman damgaları
- İlişkisel veri bütünlüğü

### Test Senaryoları
- Açık ticket'lar (test için)
- Çözülmüş ticket'lar (örnek çözümler)
- Farklı priority seviyeleri
- Farklı kategoriler

### Kullanıcı Rolleri
- Admin yetkili işlemler
- Support team üyeleri
- Normal kullanıcılar

## 🛠️ Özelleştirme

Kendi seed data'nızı eklemek için:

1. Yeni bir `@Component` ve `CommandLineRunner` sınıfı oluşturun
2. `@Order` annotation'ı ile sırasını belirleyin
3. `@Value("${app.seed.enabled:true}")` ile kontrol ekleyin

**Örnek**:

```java
@Component
@RequiredArgsConstructor
@Slf4j
@Order(7) // Son sıra
public class CustomDataSeeder implements CommandLineRunner {
    
    private final YourRepository repository;
    
    @Value("${app.seed.enabled:true}")
    private boolean enabled;
    
    @Override
    public void run(String... args) {
        if (!enabled) {
            log.debug("[CustomDataSeeder] Seeding disabled");
            return;
        }
        
        if (repository.count() > 0) {
            log.debug("[CustomDataSeeder] Data already seeded");
            return;
        }
        
        // Seed data ekleme kodu
        log.info("[CustomDataSeeder] Successfully seeded data");
    }
}
```

## ⚠️ Önemli Notlar

1. **İdempotent**: Seed data'lar birden fazla çalıştırıldığında tekrar eklenmez (count kontrolü sayesinde)
2. **Sıralı Çalışma**: `@Order` ile bağımlılıklar doğru sırada oluşturulur
3. **Production Güvenliği**: Production ortamında `app.seed.enabled=false` olmalıdır
4. **Şifre Güvenliği**: Tüm şifreler BCrypt ile hash'lenir
5. **Transaction**: Her seeder kendi transaction'ında çalışır

## 🎯 Test Senaryoları

Seed data ile test edebileceğiniz senaryolar:

- ✅ Kullanıcı login/logout
- ✅ Ticket oluşturma/güncelleme
- ✅ Ticket atama ve status değiştirme
- ✅ Yorum ekleme
- ✅ Activity log takibi
- ✅ Internal chat
- ✅ Bildirim sistemi
- ✅ Kullanıcı tercihleri
- ✅ Role-based access control
- ✅ Dashboard istatistikleri

## 📝 Lisans

Bu seed data sistemi projenin bir parçasıdır.
