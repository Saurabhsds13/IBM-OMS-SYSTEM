import { useEffect, useMemo, useState } from 'react';
import { ordersApi } from '../services/endpoints';
import { errorMessage } from '../services/api';
import { PageHeader, DataTable, StatusBadge, Spinner } from '../components/ui';
import RoleGate from '../auth/RoleGate';
import { useAuth } from '../auth/AuthContext';
import { useToast } from '../components/Toast';
import { useConfirm } from '../components/ConfirmDialog';
import { exportCsv } from '../services/csv';
import IntakeModal from './orders/IntakeModal';
import OrderDrawer from './orders/OrderDrawer';

const WRITE_ROLES = ['OPS_MANAGER', 'ADMIN'];
const STATUSES = ['PENDING', 'APPROVED', 'PARTIALLY_SHIPPED', 'SHIPPED', 'CANCELLED'];

export default function Orders() {
  const toast = useToast();
  const confirm = useConfirm();
  const { hasRole } = useAuth();
  const canWrite = hasRole(WRITE_ROLES);

  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [busyId, setBusyId] = useState(null);
  const [showIntake, setShowIntake] = useState(false);
  const [selected, setSelected] = useState(null);
  const [selectedIds, setSelectedIds] = useState(() => new Set());
  const [bulkBusy, setBulkBusy] = useState(false);

  const [status, setStatus] = useState('');
  const [search, setSearch] = useState('');

  const load = async (filters = {}) => {
    setLoading(true);
    try {
      setOrders((await ordersApi.list(filters)) || []);
      setSelectedIds(new Set());
    } catch (err) {
      toast.error(errorMessage(err, 'Failed to load orders'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const applyFilters = () => load({ status, orderNumber: search.trim() });
  const clearFilters = () => {
    setStatus('');
    setSearch('');
    load();
  };

  const act = async (fn, id, successMsg) => {
    setBusyId(id);
    try {
      await fn();
      toast.success(successMsg);
      await applyFilters();
    } catch (err) {
      toast.error(errorMessage(err, 'Action failed'));
    } finally {
      setBusyId(null);
    }
  };

  const confirmCancel = async (o) => {
    const ok = await confirm({
      title: 'Cancel order?',
      message: `Order ${o.orderNumber} will be cancelled. This cannot be undone.`,
      confirmLabel: 'Cancel order',
      cancelLabel: 'Keep order',
      danger: true,
    });
    if (ok) act(() => ordersApi.cancel(o.id), o.id, 'Order cancelled');
  };

  // --- Selection ---
  const toggleOne = (id) =>
    setSelectedIds((prev) => {
      const next = new Set(prev);
      next.has(id) ? next.delete(id) : next.add(id);
      return next;
    });

  const allSelected = orders.length > 0 && orders.every((o) => selectedIds.has(o.id));
  const toggleAll = () =>
    setSelectedIds(allSelected ? new Set() : new Set(orders.map((o) => o.id)));

  const selectedCount = selectedIds.size;

  const runBulk = async (action) => {
    const ids = [...selectedIds];
    const ok = await confirm({
      title: `${action === 'APPROVE' ? 'Approve' : 'Cancel'} ${ids.length} orders?`,
      message: `This will attempt to ${action.toLowerCase()} ${ids.length} selected order(s). Orders in an invalid state are skipped.`,
      confirmLabel: action === 'APPROVE' ? 'Approve all' : 'Cancel all',
      danger: action === 'CANCEL',
    });
    if (!ok) return;

    setBulkBusy(true);
    try {
      const result = await ordersApi.bulk(action, ids);
      if (result.failed === 0) {
        toast.success(`${result.succeeded}/${result.total} succeeded`);
      } else {
        toast.info(`${result.succeeded}/${result.total} succeeded, ${result.failed} skipped`);
      }
      await applyFilters();
    } catch (err) {
      toast.error(errorMessage(err, 'Bulk action failed'));
    } finally {
      setBulkBusy(false);
    }
  };

  const exportRows = () =>
    exportCsv(
      'orders',
      [
        { key: 'orderNumber', header: 'Order Number' },
        { key: 'status', header: 'Status' },
        { key: 'items', header: 'Item Count', value: (o) => (o.items ? o.items.length : 0) },
        { key: 'createdAt', header: 'Created At' },
      ],
      orders
    );

  const columns = useMemo(() => {
    const base = [];
    if (canWrite) {
      base.push({
        key: 'select',
        header: <input type="checkbox" checked={allSelected} onChange={toggleAll} aria-label="Select all" />,
        width: '36px',
        render: (o) => (
          <input
            type="checkbox"
            checked={selectedIds.has(o.id)}
            onChange={() => toggleOne(o.id)}
            aria-label={`Select ${o.orderNumber}`}
          />
        ),
      });
    }
    base.push(
      {
        key: 'orderNumber',
        header: 'Order #',
        render: (o) => (
          <button className="link-btn" onClick={() => setSelected(o.orderNumber)}>
            {o.orderNumber}
          </button>
        ),
      },
      { key: 'status', header: 'Status', render: (o) => <StatusBadge status={o.status} /> },
      { key: 'items', header: 'Items', render: (o) => (o.items ? o.items.length : 0) },
      {
        key: 'createdAt',
        header: 'Created',
        render: (o) => (o.createdAt ? new Date(o.createdAt).toLocaleString() : '—'),
      },
      {
        key: 'actions',
        header: '',
        render: (o) => (
          <RoleGate roles={WRITE_ROLES}>
            <div className="row gap-8">
              <button
                className="btn btn-sm"
                disabled={busyId === o.id || o.status !== 'PENDING'}
                onClick={() => act(() => ordersApi.approve(o.id), o.id, 'Order approved')}
              >
                Approve
              </button>
              <button
                className="btn btn-sm btn-danger"
                disabled={busyId === o.id || ['CANCELLED', 'SHIPPED', 'PARTIALLY_SHIPPED'].includes(o.status)}
                onClick={() => confirmCancel(o)}
              >
                Cancel
              </button>
            </div>
          </RoleGate>
        ),
      }
    );
    return base;
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [canWrite, allSelected, selectedIds, busyId]);

  return (
    <>
      <PageHeader
        title="Orders"
        subtitle="Review and manage the order lifecycle."
        actions={
          <>
            <button className="btn" onClick={exportRows} disabled={orders.length === 0}>
              Export CSV
            </button>
            <RoleGate roles={WRITE_ROLES}>
              <button className="btn btn-primary" onClick={() => setShowIntake(true)}>
                + Intake order
              </button>
            </RoleGate>
          </>
        }
      />

      <div className="card" style={{ padding: 14, marginBottom: 16 }}>
        <div className="row gap-12 wrap center">
          <input
            className="input"
            style={{ maxWidth: 240 }}
            placeholder="Search order number…"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && applyFilters()}
          />
          <select className="input" style={{ maxWidth: 200 }} value={status} onChange={(e) => setStatus(e.target.value)}>
            <option value="">All statuses</option>
            {STATUSES.map((s) => (
              <option key={s} value={s}>
                {s}
              </option>
            ))}
          </select>
          <button className="btn btn-primary" onClick={applyFilters}>
            Apply
          </button>
          <button className="btn" onClick={clearFilters}>
            Clear
          </button>
        </div>
      </div>

      {canWrite && selectedCount > 0 && (
        <div className="bulk-bar card">
          <span>{selectedCount} selected</span>
          <div className="row gap-8">
            <button className="btn btn-sm btn-primary" disabled={bulkBusy} onClick={() => runBulk('APPROVE')}>
              Approve selected
            </button>
            <button className="btn btn-sm btn-danger" disabled={bulkBusy} onClick={() => runBulk('CANCEL')}>
              Cancel selected
            </button>
            <button className="btn btn-sm" disabled={bulkBusy} onClick={() => setSelectedIds(new Set())}>
              Clear
            </button>
          </div>
        </div>
      )}

      {loading ? (
        <Spinner label="Loading orders…" />
      ) : (
        <DataTable columns={columns} rows={orders} rowKey={(o) => o.id ?? o.orderNumber} empty="No orders match." />
      )}

      {showIntake && (
        <IntakeModal
          onClose={() => setShowIntake(false)}
          onDone={() => {
            setShowIntake(false);
            applyFilters();
          }}
        />
      )}

      {selected && (
        <OrderDrawer orderNumber={selected} onClose={() => setSelected(null)} onChanged={applyFilters} />
      )}
    </>
  );
}
