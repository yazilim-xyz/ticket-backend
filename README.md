Aşağıya doğrudan kopyala-yapıştır yapabileceğin, çalışan hâliyle düzeltilmiş README.md’nin tam içeriğini veriyorum.
Token alanı .token olacak şekilde kalıcı ve net.

⸻


# Enterprise Ticket System – Backend

Spring Boot 3.5.6 · Java 21 · PostgreSQL · JWT · Docker

---

## 🚀 Çalıştırma (Docker)

```bash
docker compose build --no-cache app
docker compose up -d

	•	Backend: http://localhost:8081
	•	PostgreSQL: localhost:5433
	•	DB adı: enterprise_ticket_system

⸻

🔐 Ortam Değişkenleri (.env)

.env dosyası gitignore’da olmalıdır.

# JWT
JWT_SECRET=60b711c38b7085eab8ef9c3d20eb90b0b0ff5c68bd54fe1b5f65537360d8f811
JWT_EXPIRATION_MS=3600000
JWT_ISSUER=enterprise-ticket-system
JWT_AUDIENCE=enterprise-ticket-system-client
JWT_REFRESH_DAYS=30

# Admin Seeder
APP_SEED_ADMIN_ENABLED=true
APP_SEED_ADMIN_EMAIL=admin@local
APP_SEED_ADMIN_PASSWORD=Admin123!
APP_SEED_ADMIN_FULL_NAME=Admin

# Auth / Security
AUTH_LOGIN_MAX_ATTEMPTS=5
AUTH_LOGIN_BLOCK_MINUTES=15

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://127.0.0.1:3000


⸻

🧪 Sistem Sağlık Kontrolü

curl -i http://localhost:8081/actuator/health

Beklenen çıktı:

{"status":"UP"}


⸻

🔑 JWT Login & Admin Test Akışı

1️⃣ Login (ADMIN)

curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@local","password":"Admin123!"}'

Örnek response:

{
  "id": 1,
  "fullName": "Admin",
  "email": "admin@local",
  "role": "ADMIN",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "uuid..."
}


⸻

2️⃣ Token’ı Kalıcı Değişkene Ata

⚠️ ÖNEMLİ: Token alanı .token

TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@local","password":"Admin123!"}' | jq -r '.token')

echo "TOKEN length: ${#TOKEN}"


⸻

3️⃣ Korunan Admin Endpoint (ROLE_ADMIN)

curl -i -H "Authorization: Bearer $TOKEN" \
  http://localhost:8081/api/admin/users

Beklenen:
	•	200 OK
	•	Kullanıcı listesi JSON

⸻

4️⃣ Refresh Token Testi

REFRESH=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@local","password":"Admin123!"}' | jq -r '.refreshToken')

curl -s -X POST http://localhost:8081/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH\"}"


⸻

🗄️ Veritabanı Kontrolü

docker exec -it ticket_db psql -U postgres -d enterprise_ticket_system \
  -c "select id,email,is_active,role from users order by id;"

Beklenen:

 id |    email    | is_active | role
----+-------------+-----------+------
  1 | admin@local | t         | ADMIN


⸻

🧠 Debug / Log İnceleme

docker compose logs -f app | egrep -i \
"JwtFilter|JwtUtil|Invalid token|expired|role mismatch|forbidden|AuthService|BadCredentials"


⸻

✅ Güvenlik Notları
	•	JWT secret tek noktadan üretilir (sign & parse aynı key)
	•	HS256 → minimum 256-bit key zorunlu
	•	Aktif olmayan kullanıcılar 403
	•	Role mismatch → 401
	•	Stateless JWT (session yok)

⸻

📌 Özet

✔ Docker + PostgreSQL
✔ Admin otomatik seed
✔ JWT login / refresh çalışıyor
✔ ROLE_ADMIN koruması aktif
✔ Production’a hazır backend

⸻


---