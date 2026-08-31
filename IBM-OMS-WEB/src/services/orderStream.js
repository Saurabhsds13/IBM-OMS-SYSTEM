const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081';

/**
 * Opens an SSE connection to the OMS order-status stream. Calls onEvent with
 * the parsed OrderStatusEvent for each `order-status` message. Returns a
 * cleanup function that closes the stream.
 *
 * Note: the stream endpoint is unauthenticated by design (EventSource cannot
 * send an Authorization header) and carries only order-status notifications.
 */
export function subscribeOrderStream(onEvent) {
  const es = new EventSource(`${BASE_URL}/api/v1/admin/orders/stream`);

  es.addEventListener('order-status', (e) => {
    try {
      onEvent(JSON.parse(e.data));
    } catch {
      // ignore malformed frames
    }
  });

  return () => es.close();
}
