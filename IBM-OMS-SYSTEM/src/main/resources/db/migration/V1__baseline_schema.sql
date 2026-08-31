-- V1: Baseline schema for all OMS module tables plus the security (RBAC) tables.
-- Column names and types match Hibernate's default physical naming strategy
-- (CamelCase -> snake_case) so that spring.jpa.hibernate.ddl-auto=validate passes.

-- ---------------------------------------------------------------------------
-- Order module
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
    id           BIGINT NOT NULL AUTO_INCREMENT,
    order_number VARCHAR(255),
    status       VARCHAR(255),
    created_at   DATETIME(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS order_items (
    id               BIGINT NOT NULL AUTO_INCREMENT,
    product_code     VARCHAR(255),
    quantity         INT NOT NULL,
    shipped_quantity INT NOT NULL,
    order_id         BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- Inventory module
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS inventory (
    id            BIGINT NOT NULL AUTO_INCREMENT,
    product_code  VARCHAR(255),
    available_qty INT NOT NULL,
    reserved_qty  INT NOT NULL,
    vendor_name   VARCHAR(255),
    location      VARCHAR(255),
    demand        INT NOT NULL,
    supply        INT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- Payment module
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS payments (
    id           BIGINT NOT NULL AUTO_INCREMENT,
    order_number VARCHAR(255),
    status       VARCHAR(255),
    amount       DOUBLE NOT NULL,
    created_at   DATETIME(6),
    retry_count  INT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- Shipping module
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS shipments (
    id           BIGINT NOT NULL AUTO_INCREMENT,
    order_number VARCHAR(255),
    status       VARCHAR(255),
    carrier      VARCHAR(255),
    created_at   DATETIME(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS shipment_events (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    description VARCHAR(255),
    event_time  DATETIME(6),
    shipment_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_shipment_events_shipment FOREIGN KEY (shipment_id) REFERENCES shipments (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- Analytics module
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS daily_sales_agg (
    id            BIGINT NOT NULL AUTO_INCREMENT,
    date          DATE,
    vendor_name   VARCHAR(255),
    product_code  VARCHAR(255),
    revenue       DOUBLE NOT NULL,
    orders        BIGINT NOT NULL,
    aov           DOUBLE NOT NULL,
    cancellations BIGINT NOT NULL,
    backorders    BIGINT NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_dsa_date (date),
    INDEX idx_dsa_vendor_date (vendor_name, date),
    INDEX idx_dsa_product_date (product_code, date)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS kpi_snapshot (
    id                     BIGINT NOT NULL AUTO_INCREMENT,
    computed_at            DATETIME(6),
    revenue_last24h        DOUBLE NOT NULL,
    orders_last24h         BIGINT NOT NULL,
    aov_last24h            DOUBLE NOT NULL,
    refund_rate_last7d     DOUBLE NOT NULL,
    fulfillment_sla_hit_pct DOUBLE NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- Common / outbox
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS outbox_events (
    id             BIGINT NOT NULL AUTO_INCREMENT,
    aggregate_type VARCHAR(255),
    aggregate_id   VARCHAR(255),
    event_type     VARCHAR(255),
    payload        LONGTEXT,
    status         VARCHAR(255),
    attempt_count  INT NOT NULL,
    created_at     DATETIME(6),
    last_attempt_at DATETIME(6),
    PRIMARY KEY (id),
    INDEX idx_outbox_status (status),
    INDEX idx_outbox_created (created_at)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- Security / RBAC (User, Role, Permission)
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id              BIGINT NOT NULL AUTO_INCREMENT,
    username        VARCHAR(100) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    enabled         BIT NOT NULL,
    failed_attempts INT NOT NULL,
    lock_until      DATETIME(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS roles (
    id   BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_roles_name (name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS permissions (
    id   BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_permissions_name (name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions (id)
) ENGINE=InnoDB;
