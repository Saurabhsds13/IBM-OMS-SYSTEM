-- V3: Order status transition audit trail. Each row records one status change
-- (who, from -> to, when), written in the same transaction as the change.

CREATE TABLE IF NOT EXISTS order_status_history (
    id           BIGINT NOT NULL AUTO_INCREMENT,
    order_id     BIGINT,
    order_number VARCHAR(255) NOT NULL,
    from_status  VARCHAR(255),
    to_status    VARCHAR(255) NOT NULL,
    changed_by   VARCHAR(255) NOT NULL,
    changed_at   DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_osh_order_number (order_number),
    INDEX idx_osh_order_id (order_id)
) ENGINE=InnoDB;
