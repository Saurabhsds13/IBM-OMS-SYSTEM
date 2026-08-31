import { createContext, useContext, useEffect, useRef, useState, useCallback } from 'react';
import { subscribeOrderStream } from '../services/orderStream';

const LiveEventsContext = createContext(null);

const MAX_EVENTS = 30;

/**
 * Single app-wide SSE subscription. Accumulates recent order-status events for
 * the notification bell, tracks an unread count, and lets pages register
 * listeners so they can refresh on live changes without opening their own
 * EventSource.
 */
export function LiveEventsProvider({ children }) {
  const [events, setEvents] = useState([]);
  const [unread, setUnread] = useState(0);
  const [connected, setConnected] = useState(false);
  const listeners = useRef(new Set());

  useEffect(() => {
    const unsubscribe = subscribeOrderStream((event) => {
      setConnected(true);
      const entry = {
        id: `${event.orderNumber}-${event.occurredAt || Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
        orderNumber: event.orderNumber,
        status: event.status,
        eventType: event.eventType,
        occurredAt: event.occurredAt || new Date().toISOString(),
      };
      setEvents((prev) => [entry, ...prev].slice(0, MAX_EVENTS));
      setUnread((n) => n + 1);
      listeners.current.forEach((fn) => {
        try {
          fn(event);
        } catch {
          /* ignore listener errors */
        }
      });
    });
    return unsubscribe;
  }, []);

  const markAllRead = useCallback(() => setUnread(0), []);
  const clear = useCallback(() => {
    setEvents([]);
    setUnread(0);
  }, []);

  // Pages call this in an effect to react to live events.
  const onEvent = useCallback((fn) => {
    listeners.current.add(fn);
    return () => listeners.current.delete(fn);
  }, []);

  return (
    <LiveEventsContext.Provider value={{ events, unread, connected, markAllRead, clear, onEvent }}>
      {children}
    </LiveEventsContext.Provider>
  );
}

export function useLiveEvents() {
  const ctx = useContext(LiveEventsContext);
  if (!ctx) throw new Error('useLiveEvents must be used within LiveEventsProvider');
  return ctx;
}
