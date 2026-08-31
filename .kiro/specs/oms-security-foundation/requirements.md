# Requirements Document

## Introduction

This document defines the requirements for Phase 1 of the enterprise-grade Order Management System (OMS): the **security and foundation hardening layer**. The existing system is a Spring Boot 3.5.4 / Java 17 modular monolith backed by MySQL, comprising the order, inventory, payment, shipping, analytics, notification, and common modules. All admin endpoints under `/api/v1/admin/**` and `/api/admin/**` currently expose data and operations without authentication or authorization.

Phase 1 establishes the foundation that all later phases depend on. It introduces JWT-based authentication and role-based access control (RBAC), secures all existing admin endpoints, adds API documentation, replaces unsafe schema management with versioned migrations, externalizes secrets, and fixes three defects in the existing outbox and order-lifecycle code. Phase 1 does not add new business features beyond the security and hardening layer.

Scope confirmed with stakeholder: three default roles (ADMIN, OPS_MANAGER, VIEWER); payment and notification/outbox admin endpoints are ADMIN-only; Flyway for migrations; a seeded bootstrap ADMIN plus an admin user-management API for provisioning; account lockout after 5 failed logins for 15 minutes; access token lifetime 15 minutes and refresh token lifetime 7 days.

## Glossary

- **OMS**: The Order Management System, the Spring Boot modular monolith described above.
- **Auth_Service**: The component responsible for authenticating credentials and issuing, validating, and refreshing tokens.
- **Security_Filter**: The request-processing component that intercepts admin API requests, validates access tokens, and establishes the security context.
- **Authorization_Layer**: The method-level access-control mechanism (`@PreAuthorize`) that grants or denies access to a secured operation based on the caller's role.
- **User**: A persisted account entity with a unique username, a hashed password, an enabled flag, a failed-attempt counter, a lock-until timestamp, and one or more assigned roles.
- **Role**: A named authority (ADMIN, OPS_MANAGER, VIEWER) that groups a set of permissions.
- **Permission**: A named grant that authorizes a specific category of operation. A Role holds a set of Permissions.
- **RBAC**: Role-Based Access Control; access decisions derived from the roles assigned to a User.
- **Access_Token**: A short-lived signed JWT presented on each admin API request to prove identity and convey roles.
- **Refresh_Token**: A longer-lived token used to obtain a new Access_Token without re-entering credentials.
- **ADMIN**: Role with full access to all admin endpoints across all modules.
- **OPS_MANAGER**: Role with access to order, inventory, and shipping operations.
- **VIEWER**: Role with read-only access to analytics and order retrieval endpoints.
- **Admin_Endpoint**: Any HTTP endpoint mapped under `/api/v1/admin/**` or `/api/admin/**`.
- **OpenAPI_Service**: The springdoc-generated API documentation and Swagger UI component.
- **Migration_Service**: The Flyway component that applies versioned schema migrations at application startup.
- **OutboxDispatcher**: The scheduled component that polls pending outbox events and publishes them.
- **OrderService**: The service that manages order lifecycle transitions (approve, cancel, partial ship).
- **Configuration_Provider**: The mechanism that supplies database credentials and secrets from environment variables and Spring profiles rather than source files.

## Requirements

### Requirement 1: JWT Authentication and Login

**User Story:** As an OMS administrator, I want to authenticate with credentials and receive tokens, so that I can access secured admin endpoints.

#### Acceptance Criteria

1. WHEN a login request is submitted with a username and password that match an enabled User, THE Auth_Service SHALL return an Access_Token and a Refresh_Token with an HTTP 200 status.
2. THE Auth_Service SHALL sign each Access_Token with a secret that is supplied by the Configuration_Provider.
3. THE Auth_Service SHALL set the Access_Token expiry to 15 minutes after issuance.
4. THE Auth_Service SHALL set the Refresh_Token expiry to 7 days after issuance.
5. THE Auth_Service SHALL include the authenticated User's assigned roles as a claim in the Access_Token.
6. IF a login request is submitted with a username that has no matching User, THEN THE Auth_Service SHALL return an HTTP 401 status with an ApiResponse error envelope.
7. IF a login request is submitted with a valid username and an incorrect password, THEN THE Auth_Service SHALL return an HTTP 401 status with an ApiResponse error envelope.
8. THE Auth_Service SHALL store User passwords using a one-way adaptive hash and SHALL compare submitted passwords against the stored hash.

### Requirement 2: Token Refresh

**User Story:** As an authenticated administrator, I want to renew my access using a refresh token, so that I can continue working without re-entering credentials until the refresh token expires.

#### Acceptance Criteria

1. WHEN a refresh request is submitted with a valid, unexpired Refresh_Token, THE Auth_Service SHALL return a new Access_Token with an HTTP 200 status.
2. IF a refresh request is submitted with an expired Refresh_Token, THEN THE Auth_Service SHALL return an HTTP 401 status with an ApiResponse error envelope.
3. IF a refresh request is submitted with a Refresh_Token that fails signature validation, THEN THE Auth_Service SHALL return an HTTP 401 status with an ApiResponse error envelope.
4. THE Auth_Service SHALL set the expiry of a refreshed Access_Token to 15 minutes after issuance.

### Requirement 3: Access Token Validation on Admin Endpoints

**User Story:** As a security stakeholder, I want every admin request validated before processing, so that unauthenticated callers cannot reach protected operations.

#### Acceptance Criteria

1. WHEN a request to an Admin_Endpoint includes a valid, unexpired Access_Token, THE Security_Filter SHALL establish the security context with the token's roles and allow the request to proceed.
2. IF a request to an Admin_Endpoint omits an Access_Token, THEN THE Security_Filter SHALL reject the request with an HTTP 401 status.
3. IF a request to an Admin_Endpoint includes an expired Access_Token, THEN THE Security_Filter SHALL reject the request with an HTTP 401 status.
4. IF a request to an Admin_Endpoint includes an Access_Token that fails signature validation, THEN THE Security_Filter SHALL reject the request with an HTTP 401 status.
5. THE Security_Filter SHALL permit unauthenticated access to the login endpoint, the token refresh endpoint, and the OpenAPI documentation endpoints.

### Requirement 4: Role-Based Authorization

**User Story:** As a security stakeholder, I want each admin operation restricted to the roles permitted to perform it, so that users can only perform actions appropriate to their role.

#### Acceptance Criteria

1. WHEN an authenticated caller with the ADMIN role invokes any Admin_Endpoint, THE Authorization_Layer SHALL grant access.
2. WHEN an authenticated caller with the OPS_MANAGER role invokes an order, inventory, or shipping operation endpoint, THE Authorization_Layer SHALL grant access.
3. WHEN an authenticated caller with the VIEWER role invokes a read-only analytics or order retrieval endpoint, THE Authorization_Layer SHALL grant access.
4. IF an authenticated caller with the VIEWER role invokes an order, inventory, shipping, payment, or notification write operation, THEN THE Authorization_Layer SHALL deny access with an HTTP 403 status.
5. IF an authenticated caller with the OPS_MANAGER role invokes a payment or notification Admin_Endpoint, THEN THE Authorization_Layer SHALL deny access with an HTTP 403 status.
6. THE Authorization_Layer SHALL restrict every payment Admin_Endpoint and every notification Admin_Endpoint to the ADMIN role.
7. THE Authorization_Layer SHALL enforce authorization decisions at the method level using declarative annotations on the secured operations.

### Requirement 5: Role and Permission Model

**User Story:** As an administrator, I want roles composed of permissions, so that access grants are managed consistently across modules.

#### Acceptance Criteria

1. THE OMS SHALL persist User, Role, and Permission entities in the MySQL database.
2. THE OMS SHALL associate each User with one or more Roles.
3. THE OMS SHALL associate each Role with a set of Permissions.
4. THE OMS SHALL provide the three roles ADMIN, OPS_MANAGER, and VIEWER as seeded roles.
5. WHERE a User is assigned multiple Roles, THE Authorization_Layer SHALL grant access when any assigned Role permits the requested operation.

### Requirement 6: Brute-Force Protection

**User Story:** As a security stakeholder, I want failed login attempts throttled, so that credential-guessing attacks are slowed and locked out.

#### Acceptance Criteria

1. WHEN a login attempt for an existing User fails due to an incorrect password, THE Auth_Service SHALL increment that User's failed-attempt counter.
2. WHEN a login attempt for a User succeeds, THE Auth_Service SHALL reset that User's failed-attempt counter to zero.
3. IF a User's failed-attempt counter reaches 5, THEN THE Auth_Service SHALL lock the User account for 15 minutes.
4. WHILE a User account is locked, THE Auth_Service SHALL reject login attempts for that account with an HTTP 423 status, independent of whether the submitted password is correct.
5. WHEN the lock duration of 15 minutes has elapsed, THE Auth_Service SHALL allow login attempts for that account and SHALL reset the failed-attempt counter to zero on the next successful login.

### Requirement 7: User Provisioning

**User Story:** As an administrator, I want an initial admin account and the ability to manage users, so that the system is usable from first startup and access can be maintained.

#### Acceptance Criteria

1. WHEN the OMS starts against a database that contains no User records, THE Migration_Service SHALL create one bootstrap User assigned the ADMIN role.
2. THE Migration_Service SHALL store the bootstrap User password as a one-way adaptive hash sourced from the Configuration_Provider.
3. WHEN an authenticated caller with the ADMIN role submits a create-user request with a unique username and one or more valid roles, THE OMS SHALL persist a new enabled User with the assigned roles and return an HTTP 201 status.
4. IF an authenticated caller with the ADMIN role submits a create-user request with a username that already exists, THEN THE OMS SHALL return an HTTP 409 status with an ApiResponse error envelope.
5. IF a caller without the ADMIN role invokes any user-management endpoint, THEN THE Authorization_Layer SHALL deny access with an HTTP 403 status.

### Requirement 8: API Documentation with Security Schemes

**User Story:** As an API consumer, I want documented admin APIs with authentication details, so that I can understand and exercise the endpoints correctly.

#### Acceptance Criteria

1. THE OpenAPI_Service SHALL publish an OpenAPI description that includes every Admin_Endpoint.
2. THE OpenAPI_Service SHALL define a bearer JWT security scheme in the OpenAPI description.
3. THE OpenAPI_Service SHALL associate the bearer JWT security scheme with each secured Admin_Endpoint in the OpenAPI description.
4. THE OpenAPI_Service SHALL serve an interactive Swagger UI that allows a caller to supply an Access_Token for authenticated requests.

### Requirement 9: Versioned Database Migrations

**User Story:** As a developer, I want schema changes applied through versioned migrations, so that the database structure is deterministic and safe across environments.

#### Acceptance Criteria

1. THE OMS SHALL set the Hibernate schema-generation mode to `validate` so that Hibernate does not alter the schema at runtime.
2. WHEN the OMS starts, THE Migration_Service SHALL apply all pending versioned migrations before the application accepts requests.
3. THE Migration_Service SHALL create the schema for the User, Role, Permission, and existing module tables through versioned migration scripts.
4. IF a migration fails to apply, THEN THE Migration_Service SHALL halt application startup and report the failing migration.
5. WHEN all migrations have already been applied, THE Migration_Service SHALL start the application without modifying the schema.

### Requirement 10: Configuration Hygiene

**User Story:** As a security stakeholder, I want credentials and secrets externalized, so that no secret values are stored in source-controlled files.

#### Acceptance Criteria

1. THE Configuration_Provider SHALL supply the database URL, database username, and database password from environment variables.
2. THE Configuration_Provider SHALL supply the JWT signing secret from an environment variable.
3. THE OMS SHALL contain no hardcoded database password and no hardcoded JWT signing secret in source-controlled configuration files.
4. IF a required secret is absent at startup, THEN THE OMS SHALL halt startup and report the name of the missing configuration key.
5. THE OMS SHALL support distinct configuration values per Spring profile.

### Requirement 11: Outbox Dispatch Interval Fix

**User Story:** As an operator, I want outbox events dispatched on the intended schedule, so that downstream consumers receive events promptly.

#### Acceptance Criteria

1. THE OutboxDispatcher SHALL execute the dispatch cycle at a fixed rate of 10 seconds.
2. WHEN a dispatch cycle runs and pending outbox events exist, THE OutboxDispatcher SHALL publish each eligible pending event and mark each published event with status PUBLISHED.

### Requirement 12: Single Publish on Order Approval

**User Story:** As an operator, I want an approved order to emit exactly one approval event, so that downstream consumers do not process duplicates.

#### Acceptance Criteria

1. WHEN OrderService approves an order that is not already approved, THE OrderService SHALL record exactly one ORDER_APPROVED outbox event for that order.
2. WHEN OrderService approves an order, THE OrderService SHALL set the order status to APPROVED within a single database transaction that also records the approval event.
3. IF OrderService approves an order whose status is already APPROVED, THEN THE OrderService SHALL return an HTTP 409-mapped business error and SHALL record no additional ORDER_APPROVED event.

### Requirement 13: Transactional and Guarded Order Cancellation

**User Story:** As an operator, I want order cancellation to be transactional and state-aware, so that already-shipped or already-cancelled orders cannot be cancelled.

#### Acceptance Criteria

1. WHEN OrderService cancels an order whose status is neither SHIPPED, PARTIALLY_SHIPPED, nor CANCELLED, THE OrderService SHALL set the order status to CANCELLED within a single database transaction and return the updated order.
2. IF OrderService cancels an order whose status is CANCELLED, THEN THE OrderService SHALL return an HTTP 409-mapped business error and SHALL leave the order status unchanged.
3. IF OrderService cancels an order whose status is SHIPPED or PARTIALLY_SHIPPED, THEN THE OrderService SHALL return an HTTP 409-mapped business error and SHALL leave the order status unchanged.
4. IF the cancellation transaction fails after the status change, THEN THE OrderService SHALL roll back the status change so that the persisted order status is unmodified.
