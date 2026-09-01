-- V2: Seed the three default roles and their permissions,
-- then create a single bootstrap ADMIN user only if the users table is empty.

-- ============================================================
-- 1. Seed roles
-- ============================================================

INSERT INTO roles (name)
SELECT 'ADMIN'
WHERE NOT EXISTS (
    SELECT 1
    FROM roles
    WHERE name = 'ADMIN'
);

INSERT INTO roles (name)
SELECT 'OPS_MANAGER'
WHERE NOT EXISTS (
    SELECT 1
    FROM roles
    WHERE name = 'OPS_MANAGER'
);

INSERT INTO roles (name)
SELECT 'VIEWER'
WHERE NOT EXISTS (
    SELECT 1
    FROM roles
    WHERE name = 'VIEWER'
);


-- ============================================================
-- 2. Seed permissions
-- ============================================================

INSERT INTO permissions (name)
SELECT p.name
FROM (
    SELECT 'ORDER_READ' AS name
    UNION ALL SELECT 'ORDER_WRITE'
    UNION ALL SELECT 'INVENTORY_READ'
    UNION ALL SELECT 'INVENTORY_WRITE'
    UNION ALL SELECT 'SHIPPING_WRITE'
    UNION ALL SELECT 'ANALYTICS_READ'
    UNION ALL SELECT 'ANALYTICS_WRITE'
    UNION ALL SELECT 'PAYMENT_ADMIN'
    UNION ALL SELECT 'NOTIFICATION_ADMIN'
    UNION ALL SELECT 'USER_ADMIN'
) AS p
WHERE NOT EXISTS (
    SELECT 1
    FROM permissions x
    WHERE x.name = p.name
);


-- ============================================================
-- 3. ADMIN gets every permission
-- ============================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1
      FROM role_permissions rp
      WHERE rp.role_id = r.id
        AND rp.permission_id = p.id
  );


-- ============================================================
-- 4. OPS_MANAGER permissions
-- ============================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
    ON p.name IN (
        'ORDER_READ',
        'ORDER_WRITE',
        'INVENTORY_READ',
        'INVENTORY_WRITE',
        'SHIPPING_WRITE',
        'ANALYTICS_READ',
        'ANALYTICS_WRITE'
    )
WHERE r.name = 'OPS_MANAGER'
  AND NOT EXISTS (
      SELECT 1
      FROM role_permissions rp
      WHERE rp.role_id = r.id
        AND rp.permission_id = p.id
  );


-- ============================================================
-- 5. VIEWER permissions
-- ============================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
    ON p.name IN (
        'ORDER_READ',
        'INVENTORY_READ',
        'ANALYTICS_READ'
    )
WHERE r.name = 'VIEWER'
  AND NOT EXISTS (
      SELECT 1
      FROM role_permissions rp
      WHERE rp.role_id = r.id
        AND rp.permission_id = p.id
  );


-- ============================================================
-- 6. Bootstrap ADMIN user
-- ============================================================
-- The user is created only when the users table is empty.
-- Password is supplied as a BCrypt hash through Flyway placeholder.

INSERT INTO users (
    username,
    password,
    enabled,
    failed_attempts,
    lock_until
)
SELECT
    '${bootstrapAdminUsername}',
    '${bootstrapAdminPasswordHash}',
    1,
    0,
    NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM users
);


-- ============================================================
-- 7. Assign ADMIN role to bootstrap user
-- ============================================================

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r
    ON r.name = 'ADMIN'
WHERE u.username = '${bootstrapAdminUsername}'
  AND NOT EXISTS (
      SELECT 1
      FROM user_roles ur
      WHERE ur.user_id = u.id
        AND ur.role_id = r.id
  );