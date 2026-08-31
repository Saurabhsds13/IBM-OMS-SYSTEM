import api, { unwrap } from './api';

// --- Auth ---
export const authApi = {
  login: (username, password) =>
    api.post('/api/v1/auth/login', { username, password }).then(unwrap),
  refresh: (refreshToken) =>
    api.post('/api/v1/auth/refresh', { refreshToken }).then(unwrap),
};

// --- Dashboard ---
export const dashboardApi = {
  summary: () => api.get('/api/v1/admin/dashboard/summary').then(unwrap),
};

// --- Orders ---
export const ordersApi = {
  list: (params = {}) => {
    const q = new URLSearchParams();
    if (params.status) q.set('status', params.status);
    if (params.orderNumber) q.set('orderNumber', params.orderNumber);
    const qs = q.toString();
    return api.get(`/api/v1/admin/orders${qs ? `?${qs}` : ''}`).then(unwrap);
  },
  get: (id) => api.get(`/api/v1/admin/orders/${id}`).then(unwrap),
  byNumber: (orderNumber) =>
    api.get(`/api/v1/admin/orders/by-number/${encodeURIComponent(orderNumber)}`).then(unwrap),
  history: (orderNumber) =>
    api.get(`/api/v1/admin/orders/by-number/${encodeURIComponent(orderNumber)}/history`).then(unwrap),
  approve: (id) => api.post(`/api/v1/admin/orders/${id}/approve`).then(unwrap),
  cancel: (id) => api.post(`/api/v1/admin/orders/${id}/cancel`).then(unwrap),
  partialShip: (id, qty) =>
    api.post(`/api/v1/admin/orders/${id}/partial-ship?qty=${qty}`).then(unwrap),
  intake: (payload) =>
    api.post('/api/v1/admin/orders/intake', payload).then(unwrap),
  bulk: (action, orderIds) =>
    api.post('/api/v1/admin/orders/bulk', { action, orderIds }).then(unwrap),
};

// --- Inventory ---
export const inventoryApi = {
  list: () => api.get('/api/v1/admin/inventory').then(unwrap),
  reserve: (productCode, qty) =>
    api
      .post(`/api/v1/admin/inventory/reserve?productCode=${encodeURIComponent(productCode)}&qty=${qty}`)
      .then(unwrap),
  release: (productCode, qty) =>
    api
      .post(`/api/v1/admin/inventory/release?productCode=${encodeURIComponent(productCode)}&qty=${qty}`)
      .then(unwrap),
};

// --- Payments (ADMIN) ---
export const paymentsApi = {
  initiate: (orderNumber, amount) =>
    api
      .post(`/api/admin/payments/initiate?orderNumber=${encodeURIComponent(orderNumber)}&amount=${amount}`)
      .then(unwrap),
  refund: (id) => api.post(`/api/admin/payments/${id}/refund`).then(unwrap),
};

// --- Shipping ---
export const shippingApi = {
  create: (orderNumber, carrier) =>
    api
      .post(`/api/admin/shipping/create?orderNumber=${encodeURIComponent(orderNumber)}&carrier=${encodeURIComponent(carrier)}`)
      .then(unwrap),
  byOrder: (orderNumber) =>
    api.get(`/api/admin/shipping/order/${encodeURIComponent(orderNumber)}`).then(unwrap),
};

// --- Analytics ---
export const analyticsApi = {
  latestKpis: () => api.get('/api/admin/analytics/kpis/latest').then(unwrap),
  timeseries: (from, to) =>
    api.get(`/api/admin/analytics/timeseries?from=${from}&to=${to}`).then(unwrap),
};

// --- Notifications / Outbox (ADMIN) ---
export const notificationsApi = {
  pending: () => api.get('/api/admin/notifications/pending').then(unwrap),
  retry: (id) => api.post(`/api/admin/notifications/retry/${id}`).then(unwrap),
};

// --- Users (ADMIN) ---
export const usersApi = {
  create: (payload) => api.post('/api/v1/admin/users', payload).then(unwrap),
};
