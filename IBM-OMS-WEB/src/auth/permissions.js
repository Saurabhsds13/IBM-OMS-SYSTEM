// Central role model mirroring the OMS backend RBAC matrix.
export const ROLES = {
  ADMIN: 'ADMIN',
  OPS_MANAGER: 'OPS_MANAGER',
  VIEWER: 'VIEWER',
};

/**
 * Navigation items and the roles allowed to see them. Kept in sync with the
 * backend @PreAuthorize matrix so the UI never surfaces an action the user
 * would get a 403 for.
 */
export const NAV = [
  { key: 'dashboard', label: 'Dashboard', path: '/', roles: ['ADMIN', 'OPS_MANAGER', 'VIEWER'] },
  { key: 'orders', label: 'Orders', path: '/orders', roles: ['ADMIN', 'OPS_MANAGER', 'VIEWER'] },
  { key: 'inventory', label: 'Inventory', path: '/inventory', roles: ['ADMIN', 'OPS_MANAGER', 'VIEWER'] },
  { key: 'shipping', label: 'Shipping', path: '/shipping', roles: ['ADMIN', 'OPS_MANAGER'] },
  { key: 'analytics', label: 'Analytics', path: '/analytics', roles: ['ADMIN', 'OPS_MANAGER', 'VIEWER'] },
  { key: 'payments', label: 'Payments', path: '/payments', roles: ['ADMIN'] },
  { key: 'notifications', label: 'Notifications', path: '/notifications', roles: ['ADMIN'] },
  { key: 'users', label: 'Users', path: '/users', roles: ['ADMIN'] },
];

export function hasAnyRole(userRoles = [], allowed = []) {
  return userRoles.some((r) => allowed.includes(r));
}
