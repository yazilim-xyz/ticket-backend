# 🎯 Seed Data Özeti

## 📊 Eklenen Veriler

| Seeder | Order | Veri Sayısı | Açıklama |
|--------|-------|-------------|----------|
| **AdminSeeder** | 0 | 1 admin | Default admin hesabı (admin@local) |
| **UserSeeder** | 1 | 15 kullanıcı | 3 admin + 12 user (çalışanlar) |
| **TicketSeeder** | 2 | ~23 ticket | ADMIN'ler oluşturur, USER'lara atar |
| **TicketCommentSeeder** | 3 | ~30 yorum | Ticket'lara gerçekçi yorumlar |
| **ActivityLogSeeder** | 4 | 100+ log | Tüm aktivite kayıtları |
| **UserPreferenceSeeder** | 5 | 16 tercih | Her kullanıcı için dil, tema, bildirim |
| **InternalChatSeeder** | 6 | ~25 mesaj | Admin ve user'lar arası iletişim |

## 👥 Kullanıcı Hesapları

### Admin Hesaplar (Ticket Oluşturur)
```
Email: admin@local
Password: Admin123!
Role: ADMIN

Email: ahmet.yilmaz@enterprise.com
Password: Pass123!
Role: ADMIN

Email: elif.kaya@enterprise.com
Password: Pass123!
Role: ADMIN
```

### User Hesapları (Ticket Çözer)
```
1. mehmet.demir@enterprise.com - Pass123!
2. zeynep.celik@enterprise.com - Pass123!
3. can.ozturk@enterprise.com - Pass123!
4. ayse.sahin@enterprise.com - Pass123!
5. burak.yildiz@enterprise.com - Pass123!
6. selin.aydin@enterprise.com - Pass123!
7. emre.koc@enterprise.com - Pass123!
8. deniz.arslan@enterprise.com - Pass123!
9. fatma.gunes@enterprise.com - Pass123!
10. oguz.polat@enterprise.com - Pass123!
11. merve.kurt@enterprise.com - Pass123!
12. ali.akar@enterprise.com - Pass123!
13. ceren.yurt@enterprise.com - Pass123!
```

## 🔄 Sistem Akışı

**ADMIN rolü:**
- Ticket oluşturur
- USER'lara ticket atar
- Proje yönetimi yapar

**USER rolü:**
- Kendine atanan ticket'ları çözer
- Günlük görevlerini takip eder
- Ticket üzerinde çalışır ve günceller

## 🎫 Ticket Dağılımı

### Kategorilere Göre
| Kategori | Adet | Örnek |
|----------|------|-------|
| **BUG** | 8 | "Login sayfasında 500 hatası", "WebSocket kopuyor" |
| **FEATURE** | 5 | "Dark mode", "Dosya ekleme", "Excel export" |
| **SUPPORT** | 5 | "Şifre değiştirme", "Bildirim ayarları" |
| **OTHER** | 5 | "Sistem bakımı", "Yeni hesap açma" |

### Status'lere Göre
| Status | Adet | Açıklama |
|--------|------|----------|
| **OPEN** | ~6 | Yeni açılmış, bekleyen |
| **IN_PROGRESS** | ~5 | Üzerinde çalışılıyor |
| **RESOLVED** | ~6 | Çözülmüş |
| **CLOSED** | ~4 | Kapatılmış |
| **CANCELLED** | ~2 | İptal edilmiş |

### Priority'lere Göre
| Priority | Adet | SLA |
|----------|------|-----|
| **CRITICAL** | ~4 | Anında müdahale |
| **HIGH** | ~6 | Saatler içinde |
| **MEDIUM** | ~8 | 1-2 gün |
| **LOW** | ~5 | Haftalık |

## 💬 Örnek Chat Konuşmaları

1. **Admin ↔ Support**: Kritik ticket koordinasyonu
2. **Support ↔ Support**: WebSocket sorunu için teknik tartışma
3. **User ↔ Support**: Ticket güncelleme talebi
4. **Admin ↔ Admin**: Feature priority planlaması
5. **Team Chat**: Chatbot API sorunu çözümü

## 🔍 Test Senaryoları

### ✅ Login Test
```bash
POST /auth/login
{
  "email": "admin@local",
  "password": "Admin123!"
}
```

### ✅ Ticket Listeleme
```bash
GET /api/tickets
Authorization: Bearer <token>
```

### ✅ Kullanıcı Filtreleme
```bash
GET /api/users?role=USER
```

### ✅ Dashboard İstatistik
```bash
GET /api/dashboard/stats
```

## 🎨 Kullanıcı Tercihleri

| Özellik | Değerler | Dağılım |
|---------|----------|---------|
| **Language** | tr, en | Karışık |
| **Theme** | light, dark | %60 light, %40 dark |
| **Notifications** | true, false | %80 açık, %20 kapalı |

## 🚀 Hızlı Başlangıç

1. **Veritabanını Başlat**
```bash
docker-compose up -d postgres
```

2. **Uygulamayı Çalıştır**
```bash
./mvnw spring-boot:run
```

3. **Seed Data Otomatik Yüklenir** ✅

4. **Test Et**
```bash
# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@local","password":"Admin123!"}'

# Ticket Listele
curl -X GET http://localhost:8080/api/tickets \
  -H "Authorization: Bearer <your-token>"
```

## 📝 Özelleştirme

Seed data'yı özelleştirmek için:

1. `application.properties` düzenle:
```properties
app.seed.enabled=true
app.seed.admin.email=custom@admin.com
app.seed.admin.password=CustomPass123!
```

2. Seeder sınıflarını düzenle (örn: UserSeeder.java)

3. Yeni seeder ekle:
```java
@Component
@Order(7)
public class CustomSeeder implements CommandLineRunner { ... }
```

## ⚠️ Production Uyarısı

**Production ortamında mutlaka devre dışı bırakın:**

```properties
app.seed.enabled=false
```

Veya environment variable ile:
```bash
export APP_SEED_ENABLED=false
```

## 🎯 Seed Data Avantajları

✅ **Gerçekçi Test Verileri**: Türkçe içerik ve gerçek senaryolar  
✅ **Zaman Kazandırır**: Manuel veri girişi gerekmez  
✅ **Tutarlı Ortam**: Tüm geliştiriciler aynı veriyle çalışır  
✅ **Demo Hazır**: Anında demo yapılabilir  
✅ **İlişkisel Bütünlük**: Tüm foreign key'ler doğru  
✅ **İdempotent**: Birden fazla çalıştırma güvenli  

## 📞 İletişim

Seed data ile ilgili sorunlar için loglara bakın:
```bash
tail -f logs/application.log | grep Seeder
```

Her seeder başarılı olunca log yazar:
```
[UserSeeder] Successfully seeded 15 users
[TicketSeeder] Successfully seeded 23 tickets
...
```
