import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { authApi } from '../services/endpoints';
import { tokenStore } from '../services/api';
import { userFromToken, isExpired } from '../services/jwt';
import { hasAnyRole } from './permissions';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Restore session from a stored, unexpired access token on load.
  useEffect(() => {
    const access = tokenStore.getAccess();
    if (access && !isExpired(access)) {
      setUser(userFromToken(access));
    } else if (access) {
      // Access expired but a refresh token may still be valid; keep it and let
      // the axios interceptor refresh on the first API call.
      const refresh = tokenStore.getRefresh();
      if (refresh) setUser(userFromToken(access));
      else tokenStore.clear();
    }
    setLoading(false);
  }, []);

  const login = async (username, password) => {
    const data = await authApi.login(username, password);
    tokenStore.set(data.accessToken, data.refreshToken);
    setUser(userFromToken(data.accessToken));
  };

  const logout = () => {
    tokenStore.clear();
    setUser(null);
  };

  const value = useMemo(
    () => ({
      user,
      loading,
      isAuthenticated: !!user,
      roles: user?.roles ?? [],
      hasRole: (allowed) => hasAnyRole(user?.roles ?? [], allowed),
      login,
      logout,
    }),
    [user, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
