import { useState } from 'react';
import { ordersApi } from '../../services/endpoints';
import { errorMessage } from '../../services/api';
import Modal from '../../components/Modal';
import { useToast } from '../../components/Toast';

export default function IntakeModal({ onClose, onDone }) {
  const toast = useToast();
  const [orderNumber, setOrderNumber] = useState('');
  const [items, setItems] = useState([{ productCode: '', quantity: 1 }]);
  const [submitting, setSubmitting] = useState(false);

  const setItem = (i, patch) =>
    setItems((rows) => rows.map((r, idx) => (idx === i ? { ...r, ...patch } : r)));
  const addItem = () => setItems((r) => [...r, { productCode: '', quantity: 1 }]);
  const removeItem = (i) => setItems((r) => r.filter((_, idx) => idx !== i));

  const submit = async () => {
    const cleaned = items
      .map((it) => ({ productCode: it.productCode.trim(), quantity: Number(it.quantity) }))
      .filter((it) => it.productCode && it.quantity > 0);

    if (!orderNumber.trim()) return toast.error('Order number is required');
    if (cleaned.length === 0) return toast.error('Add at least one valid item');

    setSubmitting(true);
    try {
      await ordersApi.intake({ orderNumber: orderNumber.trim(), items: cleaned });
      toast.success('Order ingested');
      onDone();
    } catch (err) {
      toast.error(errorMessage(err, 'Intake failed'));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Modal
      title="Intake order"
      onClose={onClose}
      footer={
        <>
          <button className="btn" onClick={onClose} disabled={submitting}>
            Cancel
          </button>
          <button className="btn btn-primary" onClick={submit} disabled={submitting}>
            {submitting ? 'Submitting…' : 'Ingest'}
          </button>
        </>
      }
    >
      <div className="field">
        <label>Order number</label>
        <input
          className="input"
          value={orderNumber}
          onChange={(e) => setOrderNumber(e.target.value)}
          placeholder="e.g. QB-100234"
          autoFocus
        />
      </div>

      <label className="muted" style={{ fontSize: 12, fontWeight: 600 }}>
        Items
      </label>
      {items.map((it, i) => (
        <div key={i} className="row gap-8 center" style={{ marginTop: 8 }}>
          <input
            className="input grow"
            placeholder="Product code"
            value={it.productCode}
            onChange={(e) => setItem(i, { productCode: e.target.value })}
          />
          <input
            className="input"
            type="number"
            min="1"
            style={{ width: 90 }}
            value={it.quantity}
            onChange={(e) => setItem(i, { quantity: e.target.value })}
          />
          <button
            className="btn btn-sm"
            onClick={() => removeItem(i)}
            disabled={items.length === 1}
            title="Remove"
          >
            ×
          </button>
        </div>
      ))}
      <button className="btn btn-sm" style={{ marginTop: 10 }} onClick={addItem}>
        + Add item
      </button>
    </Modal>
  );
}
