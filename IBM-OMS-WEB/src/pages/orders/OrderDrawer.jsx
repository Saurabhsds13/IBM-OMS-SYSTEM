import { useEffect, useState } from 'react';
import { ordersApi } from '../../services/endpoints';
import { errorMessage } from '../../services/api';
import { StatusBadge, Spinner } from '../../components/ui';
import RoleGate from '../../auth/RoleGate';
import { useToast } from '../../components/Toast';
import { useConfirm } from '../../components/ConfirmDialog';
import './OrderDrawer.css';

const WRITE_ROLES = ['OPS_MANAGER', 'ADMIN'];

export default function OrderDrawer({ orderNumber, onClose, onChanged }) {
  const toast = useToast();
  const confirm = useConfirm();
  const [order, setOrder] = useState(null);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [shipQty, setShipQty] = useState({}); // productCode -> qty to ship now

  const load = async () => {
    setLoading(true);
    try {
      const [ord, hist] = await Promise.allSettled([
        ordersApi.byNumber(orderNumber),
        ordersApi.history(orderNumber),
      ]);
      if (ord.status === 'fulfilled') setOrder(ord.value);
      else toast.error(errorMessage(ord.reason, 'Failed to load order'));
      if (hist.status === 'fulfilled') setHistory(hist.value || []);
      setShipQty({});
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [orderNumber]);

  const runAction = async (fn, msg) => {
    setBusy(true);
    try {
      await fn();
      toast.success(msg);
      await load();
      onChanged?.();
    } catch (err) {
      toast.error(errorMessage(err, 'Action failed'));
    } finally {
      setBusy(false);
    }
  };

  const approve = () => runAction(() => ordersApi.approve(order.id), 'Order approved');

  const cancel = async () => {
    const ok = await confirm({
      title: 'Cancel order?',
      message: `Order ${order.orderNumber} will be cancelled. This cannot be undone.`,
      confirmLabel: 'Cancel order',
      cancelLabel: 'Keep order',
      danger: true,
    });
    if (ok) runAction(() => ordersApi.cancel(order.id), 'Order cancelled');
  };

  const canApprove = order?.status === 'PENDING';
  const canCancel = order && !['CANCELLED', 'SHIPPED', 'PARTIALLY_SHIPPED'].includes(order.status);
  const canFulfill = order && ['APPROVED', 'PARTIALLY_SHIPPED'].includes(order.status);

  const remainingFor = (item) => item.quantity - item.shippedQuantity;

  const setQty = (productCode, value, max) => {
    const n = Math.max(0, Math.min(Number(value) || 0, max));
    setShipQty((prev) => ({ ...prev, [productCode]: n }));
  };

  const fulfill = async () => {
    const lines = (order.items || [])
      .map((it) => ({ productCode: it.productCode, quantity: shipQty[it.productCode] || 0 }))
      .filter((l) => l.quantity > 0);

    if (lines.length === 0) {
      toast.error('Enter a quantity to ship for at least one item');
      return;
    }
    await runAction(() => ordersApi.fulfill(order.id, lines), 'Fulfillment recorded');
  };

  return (
    <div className="drawer-backdrop" onClick={onClose}>
      <aside className="drawer" onClick={(e) => e.stopPropagation()}>
        <div className="drawer-head">
          <div>
            <div className="muted" style={{ fontSize: 12 }}>Order</div>
            <h3>{orderNumber}</h3>
          </div>
          <button className="modal-close" onClick={onClose} aria-label="Close">×</button>
        </div>

        {loading ? (
          <Spinner label="Loading order…" />
        ) : order ? (
          <div className="drawer-body">
            <div className="row between center" style={{ marginBottom: 16 }}>
              <StatusBadge status={order.status} />
              <span className="muted" style={{ fontSize: 12 }}>
                {order.createdAt ? new Date(order.createdAt).toLocaleString() : ''}
              </span>
            </div>

            <div style={{ fontSize: 13, fontWeight: 600, margin: '8px 0' }}>Line items</div>
            <div className="table-wrap card">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Product</th>
                    <th>Ordered</th>
                    <th>Shipped</th>
                    <th>Remaining</th>
                    {canFulfill && <th>Ship now</th>}
                  </tr>
                </thead>
                <tbody>
                  {(order.items || []).map((it, i) => {
                    const remaining = remainingFor(it);
                    return (
                      <tr key={it.id ?? `${it.productCode}-${i}`}>
                        <td>{it.productCode}</td>
                        <td>{it.quantity}</td>
                        <td>{it.shippedQuantity}</td>
                        <td>{remaining}</td>
                        {canFulfill && (
                          <td>
                            <input
                              className="input"
                              type="number"
                              min="0"
                              max={remaining}
                              style={{ width: 72 }}
                              disabled={remaining === 0 || busy}
                              value={shipQty[it.productCode] ?? ''}
                              onChange={(e) => setQty(it.productCode, e.target.value, remaining)}
                            />
                          </td>
                        )}
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>

            {canFulfill && (
              <div className="row between center" style={{ marginTop: 10 }}>
                <span className="muted" style={{ fontSize: 12 }}>
                  Ship remaining units to fulfill the order.
                </span>
                <button className="btn btn-primary btn-sm" disabled={busy} onClick={fulfill}>
                  Ship selected
                </button>
              </div>
            )}

            <div style={{ fontSize: 13, fontWeight: 600, margin: '20px 0 8px' }}>Status history</div>
            {history.length === 0 ? (
              <div className="muted" style={{ fontSize: 13 }}>No history recorded.</div>
            ) : (
              <ul className="timeline">
                {history.map((h) => (
                  <li key={h.id} className="timeline-item">
                    <span className="timeline-dot" />
                    <div className="timeline-content">
                      <div className="timeline-transition">
                        {h.fromStatus ? (
                          <>
                            <StatusBadge status={h.fromStatus} />
                            <span className="timeline-arrow">→</span>
                          </>
                        ) : (
                          <span className="muted" style={{ fontSize: 12, marginRight: 6 }}>Created</span>
                        )}
                        <StatusBadge status={h.toStatus} />
                      </div>
                      <div className="timeline-meta muted">
                        {h.changedBy} · {h.changedAt ? new Date(h.changedAt).toLocaleString() : ''}
                      </div>
                    </div>
                  </li>
                ))}
              </ul>
            )}

            <RoleGate roles={WRITE_ROLES}>
              <div className="drawer-actions">
                <button className="btn btn-primary" disabled={busy || !canApprove} onClick={approve}>
                  Approve
                </button>
                <button className="btn btn-danger" disabled={busy || !canCancel} onClick={cancel}>
                  Cancel
                </button>
              </div>
            </RoleGate>
          </div>
        ) : (
          <div className="drawer-body muted">Order not found.</div>
        )}
      </aside>
    </div>
  );
}
