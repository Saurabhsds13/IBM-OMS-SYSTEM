# OMS — Technical Documentation

> Living technical document for the Order Management System (OMS) and its
> integration with the QuickBasket e-commerce system. Keep this updated as the
> project evolves.

Last updated: 2026-08-30 (Kafka/SSE integration + QuickBasket dev-branch contract verification)

---

## 1. System overview

Two independent systems that collaborate to sell and fulfill orders:

- **QuickBasket** — customer-facing e-commerce (React storefront + Spring Boot API). Handles catalog, cart, checkout, payment. Package `com.dmart.clone`. Lives in a separate repository/workspace.
- **OMS (this repo)** — internal Order Management System (Spring Boot API + React admin dashboard). Handles the order fulfillment lifecycle: intake, approval, partial shipment, cancellation, plus inventory, payments, shipping, analytics, notifications. Package `com.dmart.oms`.

The two are decoupled and communicate asynchronously over **Kafka**. Each system pushes live updates to **its own** browser UI.

---

## 2. Repositories & modules

```
OMS repo (this workspace)
├── IBM-OMS-SYSTEM/     # Spring Boot backend (Java 17, Spring Boot 3.5.4)
├── IBM-OMS-WEB/        # React admin dashboard (React 18 + Vite)
└── Docs/               # Documentation (this file, diagrams)

QuickBasket repo (separate)
├── quickbasket-api/    # Spring Boot backend (Java 17, Spring Boot 3.2)
└── quickbasket-web/    # React 19 storefront
```

---

## 3. End-to-end flow (Kafka backbone)

```
QuickBasket UI ──(REST: place/pay order)──> QuickBasket backend
                                                  │
                                                  │ produce -> Kafka topic: oms.orders.inbound
                                                  ▼
                                               KAFKA
                                                  │ consume
                                                  ▼
                                               OMS backend
                                                  │  ingest order (idempotent by orderNumber),
                                                  │  run lifecycle: approve / partial-ship / cancel
                                                  │
                                                  │ produce status changes -> Kafka topic: oms.orders.status
                                                  ▼
                                               KAFKA
                             ┌────────────────────┴────────────────────┐
                             │ consume                                  │ consume
                             ▼                                          ▼
                     QuickBasket backend                           OMS backend
                     │ push to QuickBasket UI (SSE)                │ push to OMS Admin UI (SSE)
                     ▼                                             ▼
                 QuickBasket UI                                 OMS Admin UI
                 (live order status)                            (live order status / dashboard)
```

Key points:

- **Service-to-service = Kafka.** No direct synchronous REST between the two systems in the steady-state flow (a REST intake endpoint also exists on OMS as a fallback/manual path — see §7).
- **Browser-facing live updates = SSE** (Server-Sent Events). Each backend streams events to its own UI over HTTP; the browser subscribes with `EventSource`. SSE is one-directional (server → browser), which is exactly what "live updates" needs, and requires no extra infrastructure.
- **Reliability = transactional outbox.** OMS writes domain events to an outbox table in the same DB transaction as the state change, then a scheduled dispatcher publishes them to Kafka. This guarantees an event is never lost or emitted for an uncommitted change.

---

## 4. Kafka topics & message contracts

| Topic | Producer | Consumer(s) | Purpose |
|---|---|---|---|
| `oms.orders.inbound` | QuickBasket | OMS | New orders placed by the storefront |
| `oms.orders.status` | OMS | QuickBasket, OMS (own SSE fan-out) | Order status changes during fulfillment |

Messages are JSON, **keyed by `orderNumber`** (guarantees per-order ordering within a partition).

**Inbound (QuickBasket → `oms.orders.inbound`):**
```json
{
  "eventType": "ORDER_PLACED",
  "orderNumber": "QB-100234",
  "items": [ { "productCode": "SKU-1", "quantity": 2 } ],
  "occurredAt": "2026-08-30T10:00:00Z"
}
```

**Status (OMS → `oms.orders.status`):**
```json
{
  "eventType": "ORDER_APPROVED",
  "orderNumber": "QB-100234",
  "status": "APPROVED",
  "occurredAt": "2026-08-30T10:05:00Z"
}
```

**Vocabulary (shared across both systems):**
- Order status: `PENDING`, `APPROVED`, `PARTIALLY_SHIPPED`, `SHIPPED`, `CANCELLED`
- Event types: `ORDER_PLACED`, `ORDER_APPROVED`, `ORDER_CANCELLED`, `ORDER_PARTIALLY_SHIPPED`, `ORDER_SHIPPED`

**Idempotency:** OMS intake is idempotent on `orderNumber` — re-delivering the same inbound message does not create a duplicate order. This makes Kafka's at-least-once delivery safe.

---

## 5. OMS backend (`IBM-OMS-SYSTEM`)

**Stack:** Java 17, Spring Boot 3.5.4, Spring Web, Spring Data JPA/Hibernate, Spring Security, MySQL 8, Flyway, springdoc OpenAPI, JJWT, Spring Kafka, SSE (see §8).

**Packages (`com.dmart.oms`):**
- `order` — order lifecycle, intake, status history/audit, bulk actions
- `Inventory` — stock levels, reserve/release, low-stock
- `payment` — payment operations (ADMIN)
- `shipping` — shipments and events
- `analytics` — KPIs, daily sales aggregates
- `dashboard` — aggregated operational summary
- `common` — ApiResponse envelope, exceptions, transactional outbox
- `security` — JWT auth, RBAC (User/Role/Permission), user provisioning, CORS

### 5.1 Security & RBAC
- JWT auth: 15-minute access token (carries a `roles` claim), 7-day refresh token. BCrypt password hashing.
- Roles: `ADMIN` (everything), `OPS_MANAGER` (order/inventory/shipping ops), `VIEWER` (read-only analytics + order retrieval).
- Stateless Spring Security filter chain, method-level `@PreAuthorize`, JSON 401/403 envelopes.
- Bootstrap admin seeded via Flyway migration `V2` (BCrypt hash from env).
- CORS allowed origins configurable via `OMS_CORS_ALLOWED_ORIGINS` (default the Vite dev server).

### 5.2 Transactional outbox
- `OutboxEvent` rows written in the same transaction as the domain change.
- `OutboxDispatcher` runs every 10 seconds, marks events published. This is the component that will produce to Kafka (§8).

### 5.3 Order lifecycle & audit
- Transitions: intake (`PENDING`), approve (`APPROVED`), partial ship (`PARTIALLY_SHIPPED`),
  ship (`SHIPPED`), cancel (`CANCELLED`, guarded against shipped/cancelled).
- **Every transition emits a lifecycle event** via the outbox (→ Kafka `oms.orders.status`
  + SSE): `ORDER_PLACED`, `ORDER_APPROVED`, `ORDER_PARTIALLY_SHIPPED`, `ORDER_SHIPPED`,
  `ORDER_CANCELLED`.
- `OrderService.markShipped(orderNumber)` advances an order to `SHIPPED` and is idempotent
  (no-op / no duplicate event for terminal SHIPPED/CANCELLED states). It is invoked by the
  shipping module.
- Every transition is recorded in `order_status_history` (who, from → to, when) within the
  same transaction (Flyway `V3`).
- Bulk actions apply approve/cancel to many orders, each in its own transaction so a partial
  batch does not roll back.

### 5.3a Shipping ↔ order coupling
`ShipmentService` is transactional and depends on `OrderService`:
- `createShipment(orderNumber, carrier)` creates the shipment and calls `markShipped`, so the
  order advances to `SHIPPED` and an `ORDER_SHIPPED` event flows out.
- `updateStatus(id, IN_TRANSIT|DELIVERED)` also (idempotently) ensures the order is `SHIPPED`.
This means storefront-visible status changes are driven by real fulfillment actions, not
manual order edits alone.

### 5.4 Key endpoints
| Method | Path | Roles |
|---|---|---|
| POST | `/api/v1/auth/login` | public |
| POST | `/api/v1/auth/refresh` | public |
| GET | `/api/v1/admin/dashboard/summary` | VIEWER+ |
| GET | `/api/v1/admin/orders` (`?status=&orderNumber=`) | VIEWER+ |
| GET | `/api/v1/admin/orders/by-number/{orderNumber}` | VIEWER+ |
| GET | `/api/v1/admin/orders/by-number/{orderNumber}/history` | VIEWER+ |
| POST | `/api/v1/admin/orders/intake` | OPS_MANAGER, ADMIN |
| POST | `/api/v1/admin/orders/bulk` | OPS_MANAGER, ADMIN |
| POST | `/api/v1/admin/orders/{id}/approve` `/cancel` `/partial-ship` | OPS_MANAGER, ADMIN |
| POST | `/api/v1/admin/users` | ADMIN |
| GET/POST | `/api/v1/admin/inventory/**` | VIEWER+ read / OPS_MANAGER+ write |
| POST | `/api/admin/payments/**` | ADMIN |
| POST/GET | `/api/admin/shipping/**` | OPS_MANAGER, ADMIN |
| GET/POST | `/api/admin/analytics/**` | VIEWER+ read / OPS_MANAGER+ triggers |
| GET/POST | `/api/admin/notifications/**` (outbox) | ADMIN |

### 5.5 Database migrations (Flyway)
- `V1` — baseline schema (all module tables + RBAC tables)
- `V2` — seed roles, permissions, bootstrap admin
- `V3` — `order_status_history` audit table
- Hibernate runs in `validate` mode; Flyway owns the schema.

---

## 6. OMS admin UI (`IBM-OMS-WEB`)

**Stack:** React 18, Vite, React Router 6, Axios, Chart.js.

- Axios client with JWT storage + silent refresh interceptor; unwraps the `ApiResponse` envelope.
- Role-aware navigation, routes, and actions (mirrors the backend `@PreAuthorize` matrix).
- Pages: Login, Dashboard (KPIs + charts), Orders (search/filter, detail drawer with status timeline, bulk actions, CSV export), Inventory (CSV export), Shipping, Analytics, Payments, Notifications, Users.
- Reusable UI: DataTable, SummaryCard, StatusBadge, ChartCard, Modal, Toast, ConfirmDialog.
- Dark mode via CSS variables + persisted theme toggle.
- Live SSE order-status updates on the Orders page and Dashboard (see §8).
- Icons via `lucide-react`; responsive layout (sidebar collapses to a drawer with a hamburger
  on screens <= 860px); skeleton loaders on list/summary pages.
- A single app-wide SSE subscription (`live/LiveEventsContext`) feeds a topbar notification
  bell (unread count + recent-activity dropdown) and lets pages react to live events without
  each opening their own `EventSource`.

---

## 7. Integration modes

1. **Kafka (primary, async):** the steady-state path described in §3–4.
2. **REST intake (fallback/manual):** `POST /api/v1/admin/orders/intake` lets an operator or upstream service push an order directly. Same idempotency and outbox behavior. Useful for backfills, manual entry, or if Kafka is unavailable.

---

## 8. Real-time integration (Kafka + SSE) — implemented (OMS side)

OMS-side implementation (build-verified; see §11 for runtime caveats):

1. **Kafka producer** — `common.event.KafkaOutboxPublisher` implements the existing
   `OutboxPublisher` interface and handles `ORDER` aggregate events. The existing
   `OutboxDispatcher` (every 10s) polls publishers by `canHandle` and delegates to it,
   so events are produced to `oms.orders.status` only after the originating DB
   transaction commits (reliable outbox → Kafka). Messages are keyed by order number.
2. **Kafka consumer** — `order.event.OrderInboundConsumer` (`@KafkaListener` on
   `oms.orders.inbound`, group `oms`) maps the inbound payload and calls
   `OrderService.ingestOrder(...)`. Idempotent on order number, so at-least-once
   redelivery is safe. Poison messages are logged and skipped.
3. **SSE fan-out** — `order.event.OrderEventBroadcaster` holds connected admin-UI
   emitters; `KafkaOutboxPublisher` broadcasts each `OrderStatusEvent` to them.
   `order.controller.OrderEventsController` exposes `GET /api/v1/admin/orders/stream`
   (`text/event-stream`). The path is permitted without a bearer token in
   `SecurityConfig` because `EventSource` cannot send an Authorization header; it
   carries only order-number + status notifications, no sensitive data.
4. **Topics** — `common.kafka.KafkaTopics` (constants) and `KafkaTopicConfig`
   (auto-create `NewTopic` beans, 3 partitions each).
5. **Event shape** — `order.event.OrderStatusEvent {eventType, orderNumber, status, occurredAt}`,
   with `statusForEventType(...)` mapping event type → status.
6. **Config** — `KAFKA_BOOTSTRAP_SERVERS` (default `localhost:9092`), consumer group
   `oms`, String key/value ser/deser, `OMS_KAFKA_ENABLED` flag.

**OMS UI** — `services/orderStream.js` subscribes via `EventSource`. Both the **Orders**
page (toasts each change + refreshes the filtered list) and the **Dashboard** (refreshes the
summary so counts/charts stay current) subscribe to the same stream and show a "Live"
indicator. Because every lifecycle transition emits an event (§5.3), any approve/cancel/ship
— including those triggered by shipment creation — updates both views in real time.

QuickBasket side (built in its own repo, mirrors this contract): produces `ORDER_PLACED`
to `oms.orders.inbound`, consumes `oms.orders.status`, and pushes to its storefront UI via SSE.

**Infrastructure requirement:** a running Kafka broker (e.g. local Docker on
`localhost:9092`). Code and config are build-verified, but end-to-end verification
requires a live broker + both apps + MySQL.

---

## 8a. Contract verification against QuickBasket (dev branch)

The OMS Kafka/SSE implementation was cross-checked field-by-field against the
QuickBasket `dev` branch (`quickbasket-api/.../messaging/`) on 2026-08-30. Result:
the two sides are **contract-compatible**.

| Aspect | QuickBasket (dev) | OMS | Match |
|---|---|---|---|
| Inbound topic | produces `oms.orders.inbound`, key=orderNumber | consumes same, key=orderNumber | ✅ |
| Inbound payload | `OrderInboundEvent {eventType, orderNumber, items:[{productCode, quantity}], occurredAt}` | `InboundOrder` reads same fields, ignores unknown | ✅ |
| Status topic | consumes `oms.orders.status`, key=orderNumber | produces same, key=orderNumber | ✅ |
| Status payload | `OrderStatusEvent {eventType, orderNumber, status, occurredAt}`, ignores unknown | produces same 4 fields | ✅ |
| Vocabulary | PENDING/APPROVED/PARTIALLY_SHIPPED/SHIPPED/CANCELLED; same eventTypes | identical | ✅ |
| Serialization | JSON without Spring type headers (`use.type.headers=false`) | produces header-less JSON via StringSerializer; consumes as String + Jackson | ✅ |
| Idempotency | at-least-once; expects OMS to dedupe on orderNumber | `ingestOrder` idempotent on orderNumber | ✅ |

Notes / accepted items:
- QuickBasket derives `productCode = SKU-<productId>` (no catalog SKU yet). OMS treats
  `productCode` as an opaque string, so this is fine.
- Topic names: QuickBasket externalizes via `app.kafka.topic.*` properties; OMS now does
  the same (see below) so both are config-driven with identical defaults.
- Unknown orderNumber / unknown status on the QuickBasket side are logged and skipped
  (no error) — relevant only for pre-integration orders on their side; no OMS impact.

**Config-parity change (OMS):** topic names are now resolved from
`app.kafka.topic.orders-inbound` / `app.kafka.topic.orders-status` (defaults match the
contract), used by the consumer `@KafkaListener`, the `KafkaOutboxPublisher`, and the
`NewTopic` auto-create beans. Overridable via `OMS_TOPIC_ORDERS_INBOUND` /
`OMS_TOPIC_ORDERS_STATUS`.

---

## 9. Configuration (environment variables)

| Variable | Used by | Description |
|---|---|---|
| `OMS_DB_URL` / `OMS_DB_USERNAME` / `OMS_DB_PASSWORD` | OMS backend | MySQL connection |
| `OMS_JWT_SECRET` | OMS backend | JWT signing secret (>= 32 bytes) |
| `OMS_BOOTSTRAP_ADMIN_USERNAME` / `OMS_BOOTSTRAP_ADMIN_PASSWORD_HASH` | OMS backend | Seed admin (BCrypt hash) |
| `OMS_CORS_ALLOWED_ORIGINS` | OMS backend | Allowed browser origins (default Vite dev server) |
| `KAFKA_BOOTSTRAP_SERVERS` | OMS backend | Kafka brokers (default `localhost:9092`) |
| `OMS_KAFKA_ENABLED` | OMS backend | Toggle Kafka integration (default `true`) |
| `OMS_TOPIC_ORDERS_INBOUND` | OMS backend | Inbound topic name (default `oms.orders.inbound`) |
| `OMS_TOPIC_ORDERS_STATUS` | OMS backend | Status topic name (default `oms.orders.status`) |
| `VITE_API_BASE_URL` | OMS UI | OMS API base URL (default `http://localhost:8081`) |

No secrets are committed; the backend fails fast if a required value is missing.

---

## 10. Running locally

```bash
# Backend (env vars set; MySQL running)
cd IBM-OMS-SYSTEM
./mvnw spring-boot:run          # http://localhost:8081, Swagger at /swagger-ui.html

# UI
cd IBM-OMS-WEB
npm install
npm run dev                     # http://localhost:5173
```

For the Kafka integration, additionally run a Kafka broker (e.g. via Docker Compose) reachable at `KAFKA_BOOTSTRAP_SERVERS`.

---

## 11. Status & verification notes

- All features to date are **build-verified** (backend `mvn compile`, UI `vite build`).
- **Not yet runtime-verified** against a live MySQL/Kafka + running apps. Items that especially warrant a live run: Flyway migrations, JWT login with the bootstrap admin, bulk partial-batch behavior, and (once added) the Kafka round-trip and SSE stream.
