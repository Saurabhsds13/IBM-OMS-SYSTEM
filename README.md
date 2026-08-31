# Order Management System (OMS)

## Overview
The **Order Management System (OMS)** is an enterprise-grade, modular monolith for managing orders, inventory, payments, shipping, analytics, and notifications. It is built with Spring Boot and secured with JWT-based authentication and role-based access control (RBAC). It also ingests orders handed off from upstream systems (such as the QuickBasket storefront) for fulfillment.

---

## Tech Stack
| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.4 |
| Security | Spring Security, JWT (JJWT), BCrypt |
| Persistence | Spring Data JPA / Hibernate, MySQL 8.0 |
| Migrations | Flyway (versioned SQL) |
| API Docs | springdoc OpenAPI (Swagger UI) |
| Build | Maven (wrapper included) |

---

## Modules
- **order** — order lifecycle (approve, cancel, partial ship) and inbound order intake
- **Inventory** — stock levels, reserve/release
- **payment** — payment operations (ADMIN only)
- **shipping** — shipment creation and status
- **analytics** — KPIs and daily sales aggregates
- **notification / common (outbox)** — transactional outbox and event dispatch
- **security** — JWT auth, RBAC (User/Role/Permission), user provisioning

---

## Security & Roles
Authentication is JWT-based. Access tokens carry the user's roles and expire after 15 minutes; refresh tokens last 7 days. Authorization is enforced at the method level with `@PreAuthorize`.

| Role | Access |
|---|---|
| `ADMIN` | Full access to all admin endpoints across all modules |
| `OPS_MANAGER` | Order, inventory, and shipping operations |
| `VIEWER` | Read-only analytics and order retrieval |

Payment and notification admin endpoints are restricted to `ADMIN`.

### Public endpoints (no token required)
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- Swagger UI and OpenAPI docs

---

## Key Endpoints
| Method | Path | Roles |
|---|---|---|
| `POST` | `/api/v1/auth/login` | public |
| `POST` | `/api/v1/auth/refresh` | public |
| `POST` | `/api/v1/admin/users` | ADMIN |
| `GET` | `/api/v1/admin/orders` | VIEWER, OPS_MANAGER, ADMIN |
| `POST` | `/api/v1/admin/orders/{id}/approve` | OPS_MANAGER, ADMIN |
| `POST` | `/api/v1/admin/orders/{id}/cancel` | OPS_MANAGER, ADMIN |
| `POST` | `/api/v1/admin/orders/intake` | OPS_MANAGER, ADMIN |

### Order intake (upstream handoff)
`POST /api/v1/admin/orders/intake` ingests an order placed by an upstream system. It is **idempotent** on `orderNumber`: a new order returns `201`, and a repeat with the same number returns `200` with no changes. New orders are created with status `PENDING` and emit a single `ORDER_PLACED` outbox event.

```json
{
  "orderNumber": "QB-100234",
  "items": [
    { "productCode": "SKU-APPLE-1KG", "quantity": 2 },
    { "productCode": "SKU-MILK-1L", "quantity": 1 }
  ]
}
```

Order status vocabulary: `PENDING`, `APPROVED`, `PARTIALLY_SHIPPED`, `SHIPPED`, `CANCELLED`.

---

## Getting Started

### Prerequisites
- Java 17
- Maven 3.6+ (or use the included `./mvnw` wrapper)
- MySQL 8.0+

### Configuration
All secrets are externalized to environment variables; the application fails to start if a required value is missing. See `IBM-OMS-SYSTEM/.env.example` for the full list. Required variables:

| Variable | Description |
|---|---|
| `OMS_DB_URL` | JDBC URL, e.g. `jdbc:mysql://localhost:3306/omsdb` |
| `OMS_DB_USERNAME` | Database username |
| `OMS_DB_PASSWORD` | Database password |
| `OMS_JWT_SECRET` | JWT signing secret (>= 32 bytes) |
| `OMS_BOOTSTRAP_ADMIN_USERNAME` | Bootstrap admin username (optional, defaults to `admin`) |
| `OMS_BOOTSTRAP_ADMIN_PASSWORD_HASH` | BCrypt hash of the bootstrap admin password |

On first startup against an empty database, Flyway applies the schema migrations and seeds the three roles plus one bootstrap `ADMIN` user.

### Run
```bash
git clone https://github.com/Saurabhsds13/IBM-OMS-SYSTEM.git
cd IBM-OMS-SYSTEM/IBM-OMS-SYSTEM

# set the environment variables above, then:
./mvnw spring-boot:run
```

The API starts on port `8081`. Swagger UI is available at `http://localhost:8081/swagger-ui.html`.

### Build
```bash
./mvnw clean package
```

---

## Database Migrations
Schema is managed by Flyway under `src/main/resources/db/migration`. Hibernate runs in `validate` mode and does not alter the schema at runtime. Migrations are applied automatically at startup before the application accepts requests.

---

## API Documentation
Interactive API docs are served by Swagger UI. Use the **Authorize** button to supply a bearer access token obtained from `/api/v1/auth/login`.
