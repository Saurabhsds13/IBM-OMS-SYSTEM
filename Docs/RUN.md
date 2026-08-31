# Running the OMS stack locally (end-to-end)

This guide brings up infrastructure with Docker, then runs the OMS backend and
admin UI. The Kafka round-trip additionally requires QuickBasket running (its own
repo) — but OMS runs fine on its own; it will just have nothing producing to
`oms.orders.inbound`.

## 1. Prerequisites
- Docker Desktop
- Java 17 + the Maven wrapper (`./mvnw`)
- Node 18+ / npm

## 2. Start infrastructure (MySQL + Kafka)

From the repo root:

```bash
docker compose up -d
```

This starts:
- MySQL 8 on `localhost:3306`, database `omsdb` (root password `rootpass`)
- Kafka (KRaft, single broker) on `localhost:9092`
- Kafka UI at http://localhost:8085 (optional, for inspecting topics)

Check they're healthy:

```bash
docker compose ps
```

## 3. Generate the bootstrap admin password hash

The backend needs a BCrypt hash of the admin password (never a plaintext secret).
Generate one for password `Admin@123` (or your own):

```bash
# Using Docker (no local tooling needed):
docker run --rm httpd:2.4 htpasswd -nbBC 10 admin "Admin@123" | cut -d: -f2
```

Copy the resulting `$2y$...` hash. (If your MySQL/JVM rejects `$2y$`, change the
prefix to `$2a$` — Spring Security's BCrypt accepts both.)

## 4. Set environment variables and run the backend

PowerShell (Windows):

```powershell
$env:OMS_DB_URL = "jdbc:mysql://localhost:3306/omsdb"
$env:OMS_DB_USERNAME = "root"
$env:OMS_DB_PASSWORD = "rootpass"
$env:OMS_JWT_SECRET = "local-dev-secret-change-me-at-least-32-bytes-long"
$env:OMS_BOOTSTRAP_ADMIN_USERNAME = "admin"
$env:OMS_BOOTSTRAP_ADMIN_PASSWORD_HASH = '<paste the $2a$/$2y$ hash here>'
$env:KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"

cd IBM-OMS-SYSTEM
./mvnw spring-boot:run
```

bash/macOS/Linux:

```bash
export OMS_DB_URL="jdbc:mysql://localhost:3306/omsdb"
export OMS_DB_USERNAME="root"
export OMS_DB_PASSWORD="rootpass"
export OMS_JWT_SECRET="local-dev-secret-change-me-at-least-32-bytes-long"
export OMS_BOOTSTRAP_ADMIN_USERNAME="admin"
export OMS_BOOTSTRAP_ADMIN_PASSWORD_HASH='<paste hash>'
export KAFKA_BOOTSTRAP_SERVERS="localhost:9092"

cd IBM-OMS-SYSTEM
./mvnw spring-boot:run
```

On first boot, Flyway creates the schema and seeds roles + the bootstrap admin.
The API is at http://localhost:8081, Swagger UI at
http://localhost:8081/swagger-ui.html.

## 5. Run the admin UI

```bash
cd IBM-OMS-WEB
npm install
npm run dev
```

Open http://localhost:5173 and log in with `admin` / the password you hashed.

## 6. Smoke test the round-trip

1. **Auth:** log in; confirm the dashboard loads with real counts.
2. **Order intake (REST):** in Swagger or the UI, POST an order to
   `/api/v1/admin/orders/intake`:
   ```json
   { "orderNumber": "QB-1001", "items": [ { "productCode": "SKU-1", "quantity": 2 } ] }
   ```
3. **Lifecycle + live UI:** approve the order in the UI; the Orders list and the
   notification bell should update live (SSE), and a message should appear on the
   Kafka `oms.orders.status` topic (visible in Kafka UI at :8085).
4. **Kafka inbound (with QuickBasket):** place/pay an order in QuickBasket; it
   should appear in OMS Orders as `PENDING` (consumed from `oms.orders.inbound`).

## 7. Tear down

```bash
docker compose down          # stop containers, keep data
docker compose down -v       # stop and delete MySQL/Kafka volumes (clean slate)
```

## Troubleshooting
- **Backend exits immediately on startup:** a required env var is missing (by
  design). Check `OMS_JWT_SECRET` and `OMS_BOOTSTRAP_ADMIN_PASSWORD_HASH` are set.
- **Flyway validation error:** if `omsdb` was previously created by a different
  schema, run `docker compose down -v` for a clean database.
- **UI can't reach the API / CORS error:** confirm the backend is on 8081 and
  `OMS_CORS_ALLOWED_ORIGINS` includes `http://localhost:5173` (the default).
- **No live updates:** the SSE stream is at `/api/v1/admin/orders/stream`; ensure
  the backend is reachable and no proxy is buffering event-stream responses.
