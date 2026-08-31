import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081';

const ACCESS_KEY = 'oms_access_token';
const REFRESH_KEY = 'oms_refresh_token';

export const tokenStore = {
  getAccess: () => localStorage.getItem(ACCESS_KEY),
  getRefresh: () => localStorage.getItem(REFRESH_KEY),
  set: (access, refresh) => {
    if (access) localStorage.setItem(ACCESS_KEY, access);
    if (refresh) localStorage.setItem(REFRESH_KEY, refresh);
  },
  clear: () => {
    localStorage.removeItem(ACCESS_KEY);
    localStorage.removeItem(REFRESH_KEY);
  },
};

const api = axios.create({ baseURL: BASE_URL });

// Attach the bearer access token to every request.
api.interceptors.request.use((config) => {
  const token = tokenStore.getAccess();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Silent refresh on 401. Queue concurrent requests while a refresh is in flight
// so we only refresh once.
let refreshing = null;

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config;
    const status = error.response?.status;

    const isAuthCall =
      original?.url?.includes('/api/v1/auth/login') ||
      original?.url?.includes('/api/v1/auth/refresh');

    if (status === 401 && !original._retry && !isAuthCall) {
      const refreshToken = tokenStore.getRefresh();
      if (!refreshToken) {
        handleAuthFailure();
        return Promise.reject(error);
      }

      original._retry = true;
      try {
        if (!refreshing) {
          refreshing = axios
            .post(`${BASE_URL}/api/v1/auth/refresh`, { refreshToken })
            .then((res) => {
              const data = res.data?.data ?? res.data;
              tokenStore.set(data.accessToken, data.refreshToken ?? refreshToken);
              return data.accessToken;
            })
            .finally(() => {
              refreshing = null;
            });
        }
        const newAccess = await refreshing;
        original.headers.Authorization = `Bearer ${newAccess}`;
        return api(original);
      } catch (refreshErr) {
        handleAuthFailure();
        return Promise.reject(refreshErr);
      }
    }

    return Promise.reject(error);
  }
);

function handleAuthFailure() {
  tokenStore.clear();
  if (window.location.pathname !== '/login') {
    window.location.assign('/login');
  }
}

/**
 * Unwraps the OMS ApiResponse envelope { success, message, data } when present,
 * otherwise returns the raw body.
 */
export function unwrap(response) {
  const body = response.data;
  if (body && typeof body === 'object' && 'success' in body && 'data' in body) {
    return body.data;
  }
  return body;
}

/** Extracts a human-readable message from an axios error. */
export function errorMessage(error, fallback = 'Something went wrong') {
  return (
    error?.response?.data?.message ||
    error?.response?.data?.error ||
    error?.message ||
    fallback
  );
}

export default api;
