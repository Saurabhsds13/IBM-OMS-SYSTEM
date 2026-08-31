import { useEffect, useRef, useState } from 'react';
import { Bell } from 'lucide-react';
import { useLiveEvents } from '../live/LiveEventsContext';
import { StatusBadge } from './ui';
import './NotificationBell.css';

export default function NotificationBell() {
  const { events, unread, markAllRead, clear } = useLiveEvents();
  const [open, setOpen] = useState(false);
  const ref = useRef(null);

  // Close on outside click.
  useEffect(() => {
    const onClick = (e) => {
      if (ref.current && !ref.current.contains(e.target)) setOpen(false);
    };
    document.addEventListener('mousedown', onClick);
    return () => document.removeEventListener('mousedown', onClick);
  }, []);

  const toggle = () => {
    setOpen((o) => {
      if (!o) markAllRead();
      return !o;
    });
  };

  return (
    <div className="bell" ref={ref}>
      <button className="bell-btn" onClick={toggle} aria-label="Notifications">
        <Bell size={18} />
        {unread > 0 && <span className="bell-badge">{unread > 9 ? '9+' : unread}</span>}
      </button>

      {open && (
        <div className="bell-panel card">
          <div className="bell-head">
            <span>Live activity</span>
            {events.length > 0 && (
              <button className="link-btn" onClick={clear}>
                Clear
              </button>
            )}
          </div>
          <div className="bell-list">
            {events.length === 0 ? (
              <div className="bell-empty muted">No recent activity.</div>
            ) : (
              events.map((e) => (
                <div key={e.id} className="bell-item">
                  <div className="bell-item-top">
                    <strong>{e.orderNumber}</strong>
                    <StatusBadge status={e.status} />
                  </div>
                  <div className="bell-item-meta muted">
                    {e.eventType} · {new Date(e.occurredAt).toLocaleTimeString()}
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
}
