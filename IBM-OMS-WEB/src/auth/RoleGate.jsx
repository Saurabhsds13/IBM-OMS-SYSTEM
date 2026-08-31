import { useAuth } from './AuthContext';

/**
 * Renders children only if the current user holds one of the allowed roles.
 * Used to hide write actions from VIEWER, payments from non-admins, etc.
 */
export default function RoleGate({ roles, children, fallback = null }) {
  const { hasRole } = useAuth();
  return hasRole(roles) ? children : fallback;
}
