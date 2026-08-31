import { useState } from 'react';
import { shippingApi } from '../services/endpoints';
import { errorMessage } from '../services/api';
import { PageHeader, DataTable, StatusBadge, EmptyState } from '../components/ui';
import { useToast } from '../components/Toast';

export default function Shipping() {
  const toast = useToast();
  const [orderNumber, setOrderNumber] = useState('');
  const [shipments, setShipments] = useState(null);
  const [carrier, setCarrier] = useState('');
  const [busy, setBusy] = useState(false);

  const lookup = async () => {
    if (!orderNumber.trim()) return toast.error('Enter an order number');
    setBusy(true);
    try {
      setShipments((await shippingApi.byOrder(orderNumber.trim())) || []);
    } catch (err) {
      toast.error(errorMessage(err, 'Lookup failed'));
    } finally {
      setBusy(false);
    }
  };

  const create = async () => {
    if (!orderNumber.trim() || !carrier.trim()) return toast.error('Order number and carrier are required');
    setBusy(true);
    try {
      await shippingApi.create(orderNumber.trim(), carrier.trim());
      toast.success('Shipment created');
      setCarrier('');
      await lookup();
    } catch (err) {
      toast.error(errorMessage(err, 'Create failed'));
    } finally {
      setBusy(false);
    }
  };

  const columns = [
    { key: 'id', header: 'ID' },
    { key: 'orderNumber', header: 'Order #' },
    { key: 'carrier', header: 'Carrier' },
    { key: 'status', header: 'Status', render: (s) => <StatusBadge status={s.status} /> },
    {
      key: 'createdAt',
      header: 'Created',
      render: (s) => (s.createdAt ? new Date(s.createdAt).toLocaleString() : '—'),
    },
  ];

  return (
    <>
      <PageHeader title="Shipping" subtitle="Look up and create shipments by order." />
      <div className="card" style={{ padding: 16, marginBottom: 20 }}>
        <div className="row gap-12 wrap center">
          <input
            className="input"
            style={{ maxWidth: 220 }}
            placeholder="Order number"
            value={orderNumber}
            onChange={(e) => setOrderNumber(e.target.value)}
          />
          <button className="btn" onClick={lookup} disabled={busy}>
            Look up
          </button>
          <div style={{ width: 1, alignSelf: 'stretch', background: 'var(--border)' }} />
          <input
            className="input"
            style={{ maxWidth: 180 }}
            placeholder="Carrier (e.g. FedEx)"
            value={carrier}
            onChange={(e) => setCarrier(e.target.value)}
          />
          <button className="btn btn-primary" onClick={create} disabled={busy}>
            Create shipment
          </button>
        </div>
      </div>

      {shipments === null ? (
        <EmptyState message="Enter an order number to view its shipments." />
      ) : (
        <DataTable columns={columns} rows={shipments} rowKey={(s) => s.id} empty="No shipments for this order." />
      )}
    </>
  );
}
