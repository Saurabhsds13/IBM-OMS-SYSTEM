-- V2: Seed the three default roles and their permissions, then create a single
-- bootstrap ADMIN user only if the users table is empty (Requirements 5.4, 7.1, 7.2).

-- Seed roles (Requirement 5.4).
INSERT INTO roles (name)
SELECT * FROM (SELECT 'ADMIN') AS t
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');

INSERT INTO roles (name)
SELECT * FROM (SELECT 'OPS_MANAGER') AS t
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'OPS_MANAGER');

INSERT INTO roles (name)
SELECT * FROM (SELECT 'VIEWER') AS t
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'VIEWER');

-- Seed permissions (categories of operation across modules).
INSERT INTO permissions (name)
SELECT * FROM (
    SELECT 'ORDER_READ'      UNION ALL
    SELECT 'ORDER_WRITE'     UNION ALL
    SELECT 'INVENTORY_READ'  UNION ALL
    SELECT 'INVENTORY_WRITE' UNION ALL
    SELECT 'SHIPPING_WRITE'  UNION ALL
    SELECT 'ANALYTICS_READ'  UNION ALL
    SELECT 'ANALYTICS_WRITE' UNION ALL
    SELECT 'PAYMENT_ADMIN'   UNION ALL
    SELECT 'NOTIFICATION_ADMIN' UNION ALL
    SELECT 'USER_ADMIN'
) AS p
WHERE NOT EXISTS (SELECT 1 FROM permissions x WHERE x.name = p.name);

-- ADMIN gets every permission.
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r CROSS JOIN permissions p
WHERE r.name = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id);

-- OPS_MANAGER: order, inventory, shipping operations (+ reads, analytics).
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r JOIN permissions p
  ON p.name IN ('ORDER_READ','ORDER_WRITE','INVENTORY_READ','INVENTORY_WRITE',
                'SHIPPING_WRITE','ANALYTICS_READ','ANALYTICS_WRITE')
WHERE r.name = 'OPS_MANAGER'
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id);

-- VIEWER: read-only analytics and order retrieval.
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r JOIN permissions p
  ON p.name IN ('ORDER_READ','INVENTORY_READ','ANALYTICS_READ')
WHERE r.name = 'VIEWER'
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id);

-- Bootstrap ADMIN user, created only when no users exist yet (Requirement 7.1).
-- The password is a BCrypt hash supplied by the Configuration_Provider via a
-- Flyway placeholder (Requirement 7.2); no plaintext secret is stored here.
INSERT INTO users (username, password, enabled, failed_attempts, lock_until)
SELECT '${bootstrapAdminUsername}', '${bootstrapAdminPasswordHash}', 1, 0, NULL
WHERE NOT EXISTS (SELECT 1 FROM users);

-- Assign the ADMIN role to the bootstrap user.
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u JOIN roles r ON r.name = 'ADMIN'
WHERE u.username = '${bootstrapAdminUsername}'
  AND NOT EXISTS (
      SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id);
